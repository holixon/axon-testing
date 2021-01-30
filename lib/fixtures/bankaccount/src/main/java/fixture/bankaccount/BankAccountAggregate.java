package fixture.bankaccount;

import static java.util.Optional.ofNullable;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;


import java.util.Collections;

import fixture.bankaccount.command.*;
import fixture.bankaccount.event.*;
import fixture.bankaccount.exception.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Slf4j
public class BankAccountAggregate {

  public enum Configuration {
    ;

    public static final int DEFAULT_INITIAL_BALANCE = 0;
    public static final int DEFAULT_MAXIMAL_BALANCE = 1000;
    public static final int MAXIMUM_NUMBER_OF_ACTIVE_MONEY_TRANSFERS = 1;

  }

  @AggregateIdentifier
  private String accountId;

  private Integer currentBalance;

  private Integer maximalBalance;

  private ActiveMoneyTransfer activeMoneyTransfer;

  @CommandHandler
  public static BankAccountAggregate handle(CreateAccountCommand cmd) {
    apply(AccountCreatedEvent.builder()
      .accountId(cmd.getAccountId())
      .customerId(cmd.getCustomerId())
      .initialBalance(ofNullable(cmd.getInitialBalance()).orElse(Configuration.DEFAULT_INITIAL_BALANCE))
      .maximalBalance(ofNullable(cmd.getMaximalBalance()).orElse(Configuration.DEFAULT_MAXIMAL_BALANCE))
      .build());

    return new BankAccountAggregate();
  }

  @EventSourcingHandler
  void on(AccountCreatedEvent evt) {
    log.info("replaying event: {}", evt);
    this.accountId = evt.getAccountId();
    this.currentBalance = evt.getInitialBalance();
    this.maximalBalance = evt.getMaximalBalance();
  }


  @CommandHandler
  void handle(WithdrawMoneyCommand cmd) {
    checkForInsufficientBalance(cmd.getAmount());

    apply(
      MoneyWithdrawnEvent.builder()
        .accountId(accountId)
        .amount(cmd.getAmount())
        .build()
    );
  }

  @EventSourcingHandler
  void on(MoneyWithdrawnEvent evt) {
    log.info("replaying event: {}", evt);
    decreaseCurrentBalance(evt.getAmount());
  }

  @CommandHandler
  void handle(DepositMoneyCommand cmd) {
    checkForMaximalBalanceExceeded(cmd.getAmount());

    apply(
      MoneyDepositedEvent.builder()
        .accountId(accountId)
        .amount(cmd.getAmount())
        .build()
    );
  }


  @EventSourcingHandler
  void on(MoneyDepositedEvent evt) {
    log.info("replaying event: {}", evt);
    increaseCurrentBalance(evt.getAmount());
  }


  @CommandHandler
  void handle(InitializeMoneyTransferCommand cmd) {
    checkForActiveMoneyTransfer();
    checkForInsufficientBalance(cmd.getAmount());

    apply(MoneyTransferInitializedEvent.builder()
      .transactionId(cmd.getTransactionId())
      .sourceAccountId(cmd.getSourceAccountId())
      .targetAccountId(cmd.getTargetAccountId())
      .amount(cmd.getAmount())
      .build());
  }

  @EventSourcingHandler
  void on(MoneyTransferInitializedEvent evt) {
    log.info("replaying event: {}", evt);
    this.activeMoneyTransfer = ActiveMoneyTransfer.builder()
      .transactionId(evt.getTransactionId())
      .amount(evt.getAmount())
      .build();
  }

  @CommandHandler
  void handle(ReceiveMoneyTransferCommand cmd) {
    checkForMaximalBalanceExceeded(cmd.getAmount());

    apply(MoneyTransferReceivedEvent.builder()
      .sourceAccountId(cmd.getSourceAccountId())
      .targetAccountId(cmd.getTargetAccountId())
      .transactionId(cmd.getTransactionId())
      .amount(cmd.getAmount())
      .build()
    );
  }

  @EventSourcingHandler
  void on(MoneyTransferReceivedEvent evt) {
    log.info("replaying event: {}", evt);
    increaseCurrentBalance(evt.getAmount());
  }

  @CommandHandler
  void handle(CompleteMoneyTransferCommand cmd) {
    if (activeMoneyTransfer == null || !activeMoneyTransfer.transactionId.equals(cmd.getTransactionId())) {
      throw new IllegalStateException("not participating in transaction: " + cmd.getTransactionId());
    }

    apply(MoneyTransferCompletedEvent.builder()
      .sourceAccountId(cmd.getSourceAccountId())
      .transactionId(cmd.getTransactionId())
      .amount(cmd.getAmount())
      .build()
    );
  }

  @EventSourcingHandler
  void on(MoneyTransferCompletedEvent evt) {
    decreaseCurrentBalance(evt.getAmount());
    activeMoneyTransfer = null;
  }


  @CommandHandler
  void handle(RollBackMoneyTransferCommand cmd) {
    if (activeMoneyTransfer == null || !activeMoneyTransfer.transactionId.equals(cmd.getTransactionId())) {
      throw new IllegalStateException("not participating in transaction: " + cmd.getTransactionId());
    }

    apply(MoneyTransferRolledBackEvent.builder()
      .sourceAccountId(cmd.getSourceAccountId())
      .transactionId(cmd.getTransactionId())
      .build()
    );
  }


  @EventSourcingHandler
  void on(MoneyTransferRolledBackEvent evt) {
    activeMoneyTransfer = null;
  }


  /**
   * @return stored current balance minus amount(s) reserved by active money transfers
   */
  public int getEffectiveCurrentBalance() {
    return currentBalance - ofNullable(activeMoneyTransfer).map(ActiveMoneyTransfer::getAmount).orElse(0);
  }

  private void increaseCurrentBalance(int amount) {
    this.currentBalance += amount;
    apply(BalanceChangedEvent.builder()
      .accountId(accountId)
      .newBalance(this.currentBalance)
      .build());
  }

  private void decreaseCurrentBalance(int amount) {
    increaseCurrentBalance(-amount);
  }

  private void checkForMaximalBalanceExceeded(int amount) {
    if (maximalBalance < currentBalance + amount) {
      throw new MaximalBalanceExceededException(currentBalance, amount, maximalBalance);
    }
  }

  private void checkForActiveMoneyTransfer() {
    if (activeMoneyTransfer != null) {
      throw new MaximumActiveMoneyTransfersReachedException(Configuration.MAXIMUM_NUMBER_OF_ACTIVE_MONEY_TRANSFERS,
        Collections.singletonList(activeMoneyTransfer.transactionId));
    }
  }

  private void checkForInsufficientBalance(int amount) {
    if (getEffectiveCurrentBalance() < amount) {
      throw new InsufficientBalanceException(getEffectiveCurrentBalance(), amount);
    }

  }


  @Value
  @Builder
  public static class ActiveMoneyTransfer {

    @NonNull
    private String transactionId;
    private int amount;
  }
}
