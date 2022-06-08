package io.holixon.axon.testing.examples.jgiven.junit5.java;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import fixture.bankaccount.BankAccountAggregate;
import fixture.bankaccount.command.DepositMoneyCommand;
import fixture.bankaccount.command.InitializeMoneyTransferCommand;
import fixture.bankaccount.command.WithdrawMoneyCommand;
import fixture.bankaccount.event.BalanceChangedEvent;
import fixture.bankaccount.event.MoneyDepositedEvent;
import fixture.bankaccount.event.MoneyTransferInitializedEvent;
import fixture.bankaccount.event.MoneyWithdrawnEvent;
import fixture.bankaccount.exception.InsufficientBalanceException;
import fixture.bankaccount.exception.MaximalBalanceExceededException;
import fixture.bankaccount.exception.MaximumActiveMoneyTransfersReachedException;
import io.holixon.axon.testing.jgiven.AxonJGivenJava;
import io.holixon.axon.testing.jgiven.junit5.AggregateFixtureScenarioTest;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static fixture.bankaccount.AccountAggregateTestHelper.*;
import static fixture.bankaccount.BankAccountAggregate.Configuration.DEFAULT_INITIAL_BALANCE;
import static fixture.bankaccount.BankAccountAggregate.Configuration.DEFAULT_MAXIMAL_BALANCE;

public class BankAccountAggregateJgivenJavaTest extends AggregateFixtureScenarioTest<BankAccountAggregate> {

  @ProvidedScenarioState
  private final AggregateTestFixture<BankAccountAggregate> fixture = AxonJGivenJava.aggregateTestFixtureBuilder(BankAccountAggregate.class)
    .build();

  @Test
  public void create_account() {
    given()
      .noPriorActivity()
    ;

    when()
      .command(createAccountCommand(ACCOUNT_ID_1, CUSTOMER_ID_1))
    ;

    then()
      .expectEvent(
        accountCreatedEvent(ACCOUNT_ID_1, CUSTOMER_ID_1)
      )
      .and()
      .expectState(BankAccountAggregate.builder()
        .accountId(ACCOUNT_ID_1)
        .maximalBalance(DEFAULT_MAXIMAL_BALANCE)
        .currentBalance(DEFAULT_INITIAL_BALANCE)
        .build()
      )
    ;
  }

  @Test
  public void withdraw_amount() {
    given()
      .event(accountCreatedEvent(ACCOUNT_ID_1, CUSTOMER_ID_1).toBuilder()
        .initialBalance(200)
        .build()
      )
    ;

    when()
      .command(WithdrawMoneyCommand.builder().accountId(ACCOUNT_ID_1).amount(50).build())
    ;

    then()
      .expectEvents(
        MoneyWithdrawnEvent.builder().accountId(ACCOUNT_ID_1).amount(50).build(),
        BalanceChangedEvent.builder().accountId(ACCOUNT_ID_1).newBalance(150).build()
      )
      .and()
      .expectState(
        accountAggregate(ACCOUNT_ID_1, 150, DEFAULT_MAXIMAL_BALANCE)
      )
    ;
  }

  @Test
  public void withdraw_amount_insufficientBalance() {
    given()
      .event(accountCreatedEvent(ACCOUNT_ID_1, CUSTOMER_ID_1).toBuilder()
        .initialBalance(100)
        .build()
      )
    ;

    when()
      .command(WithdrawMoneyCommand.builder().accountId(ACCOUNT_ID_1).amount(200).build())
    ;

    then()
      .expectException(
        InsufficientBalanceException.class
      )
      .expectExceptionMessage(
        "Insufficient balance, was:100, required:200"
      )
    ;
  }


  @Test
  public void deposit_money() {
    given()
      .event(accountCreatedEvent(ACCOUNT_ID_1, CUSTOMER_ID_1))
    ;
    when()
      .command(DepositMoneyCommand.builder().accountId(ACCOUNT_ID_1).amount(100).build())
    ;
    then()
      .expectEvents(
        MoneyDepositedEvent.builder().accountId(ACCOUNT_ID_1).amount(100).build(),
        BalanceChangedEvent.builder().accountId(ACCOUNT_ID_1).newBalance(100).build()
      )
      .and()
      .expectState(
        accountAggregate(ACCOUNT_ID_1, 100, DEFAULT_MAXIMAL_BALANCE)
      )
    ;
  }

  @Test
  public void we_must_not_deposit_more_than_maximalBalance() {
    given()
      .event(accountCreatedEvent(ACCOUNT_ID_1, CUSTOMER_ID_1)
        .toBuilder().maximalBalance(100).build()
      )
    ;

    when().command(DepositMoneyCommand.builder()
      .accountId(ACCOUNT_ID_1)
      .amount(101)
      .build()
    );

    then()
      .expectException(
        MaximalBalanceExceededException.class
      )
      .and()
      .expectExceptionMessage(
        "MaximalBalance exceeded: currentBalance=0, amount=101, maximalBalance=100"
      )
    ;

  }

  @Test
  public void cannot_have_more_than_one_active_transfer() {
    String transactionId = UUID.randomUUID().toString();

    given()
      .events(accountCreatedEvent(ACCOUNT_ID_1, CUSTOMER_ID_1).toBuilder()
          .initialBalance(1000).build(),
        MoneyTransferInitializedEvent.builder()
          .transactionId(transactionId)
          .targetAccountId(ACCOUNT_ID_2)
          .sourceAccountId(ACCOUNT_ID_1)
          .amount(100)
          .build()
      )
    ;

    when()
      .command(
        InitializeMoneyTransferCommand.builder()
          .sourceAccountId(ACCOUNT_ID_1)
          .targetAccountId(ACCOUNT_ID_2)
          .amount(100)
          .build()
      )
    ;

    then()
      .expectException(MaximumActiveMoneyTransfersReachedException.class)
    ;
  }

  @Test
  public void cannot_initialize_moneyTransfer_on_insufficient_balance() {
    String transactionId = UUID.randomUUID().toString();

    given()
      .event(
        accountCreatedEvent(ACCOUNT_ID_1, CUSTOMER_ID_1).toBuilder()
          .initialBalance(100).build()
      )
    ;

    when()
      .command(
        InitializeMoneyTransferCommand.builder()
          .sourceAccountId(ACCOUNT_ID_1)
          .targetAccountId(ACCOUNT_ID_2)
          .transactionId(transactionId)
          .amount(200)
          .build()
      )
    ;

    then()
      .expectException(InsufficientBalanceException.class)
    ;
  }

  @Test
  public void initialize_moneyTransfer() {
    String transactionId = UUID.randomUUID().toString();

    given()
      .event(
        accountCreatedEvent(ACCOUNT_ID_1, CUSTOMER_ID_1).toBuilder()
          .initialBalance(100).build()
      )
    ;

    when()
      .command(
        InitializeMoneyTransferCommand.builder()
          .sourceAccountId(ACCOUNT_ID_1)
          .targetAccountId(ACCOUNT_ID_2)
          .transactionId(transactionId)
          .amount(50)
          .build()
      );

    then()
      .expectEvents(
        MoneyTransferInitializedEvent.builder()
          .sourceAccountId(ACCOUNT_ID_1)
          .targetAccountId(ACCOUNT_ID_2)
          .transactionId(transactionId)
          .amount(50)
          .build()
      )
      .and()
    // FIXME: expectState consumer
//      .expectState(a -> assertThat(a.getActiveMoneyTransfer())
//        .isEqualTo(BankAccountAggregate.ActiveMoneyTransfer.builder()
//          .amount(50)
//          .transactionId(transactionId)
//          .build())
//      )
    ;
  }

  @Test
  public void cannot_withdraw_if_amount_reserved_for_active_tranfer() {
    String transactionId = UUID.randomUUID().toString();

    given()
      .events(
        accountCreatedEvent(ACCOUNT_ID_1, CUSTOMER_ID_1).toBuilder()
          .initialBalance(100).build(),
        MoneyTransferInitializedEvent.builder().sourceAccountId(ACCOUNT_ID_1).targetAccountId(ACCOUNT_ID_2).transactionId(transactionId)
          .amount(50).build()
      )
    ;

    when()
      .command(WithdrawMoneyCommand.builder().accountId(ACCOUNT_ID_1).amount(100).build())
    ;

    then()
      .expectException(InsufficientBalanceException.class)
    ;
  }
}
