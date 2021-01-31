package fixture.bankaccount;

import fixture.bankaccount.command.CreateAccountCommand;
import fixture.bankaccount.event.AccountCreatedEvent;

import java.util.UUID;

import static fixture.bankaccount.BankAccountAggregate.Configuration.DEFAULT_INITIAL_BALANCE;
import static fixture.bankaccount.BankAccountAggregate.Configuration.DEFAULT_MAXIMAL_BALANCE;

public enum AccountAggregateTestHelper {
  ;

  public static final String ACCOUNT_ID_1 = UUID.randomUUID().toString();
  public static final String CUSTOMER_ID_1 = UUID.randomUUID().toString();

  public static final String ACCOUNT_ID_2 = UUID.randomUUID().toString();
  public static final String CUSTOMER_ID_2 = UUID.randomUUID().toString();

  public static AccountCreatedEvent accountCreatedEvent(String accountId, String customerId) {
    return AccountCreatedEvent.builder()
      .accountId(accountId)
      .customerId(customerId)
      .maximalBalance(DEFAULT_MAXIMAL_BALANCE)
      .initialBalance(DEFAULT_INITIAL_BALANCE)
      .build();
  }

  public static CreateAccountCommand createAccountCommand(String accountId, String customerId) {
    return CreateAccountCommand.builder()
      .accountId(accountId)
      .customerId(customerId)
      .build();
  }

  public static BankAccountAggregate accountAggregate(String accountId, int currentBalance, int maximalBalance) {
    return BankAccountAggregate.builder()
      .accountId(accountId)
      .currentBalance(currentBalance)
      .maximalBalance(maximalBalance)
      .build();
  }
}
