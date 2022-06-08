package io.holixon.axon.testing.examples.jgiven.junit5.java;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import fixture.bankaccount.MoneyTransferSaga;
import fixture.bankaccount.event.MoneyTransferInitializedEvent;
import io.holixon.axon.testing.jgiven.junit5.SagaFixtureScenarioTest;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.jupiter.api.Test;

import static fixture.bankaccount.AccountAggregateTestHelper.ACCOUNT_ID_1;
import static fixture.bankaccount.AccountAggregateTestHelper.ACCOUNT_ID_2;
import static fixture.bankaccount.AccountAggregateTestHelper.CUSTOMER_ID_1;
import static fixture.bankaccount.AccountAggregateTestHelper.CUSTOMER_ID_2;
import static fixture.bankaccount.AccountAggregateTestHelper.accountCreatedEvent;
import static io.holixon.axon.testing.jgiven.AxonJGivenJava.sagaTestFixtureBuilder;

public class MoneyTransferSagaJgivenJavaTest extends SagaFixtureScenarioTest<MoneyTransferSaga> {

  @ProvidedScenarioState
  private final SagaTestFixture<MoneyTransferSaga> fixture = sagaTestFixtureBuilder(MoneyTransferSaga.class).build();

  @Test
  public void initialize_transfer_money() {
    given()
      .aggregatePublishedEvent(ACCOUNT_ID_1, accountCreatedEvent(ACCOUNT_ID_1, CUSTOMER_ID_1).toBuilder()
        .initialBalance(2000).build()
      )
      .and()
      .aggregatePublishedEvent(ACCOUNT_ID_2, accountCreatedEvent(ACCOUNT_ID_2, CUSTOMER_ID_2))
    ;

    when()
      .aggregatePublishes(ACCOUNT_ID_1, MoneyTransferInitializedEvent.builder()
        .sourceAccountId(ACCOUNT_ID_1)
        .targetAccountId(ACCOUNT_ID_2)
        .transactionId("1")
        .amount(100)
        .build())
    ;

    then()
      .expectActiveSagas(1)
    ;

  }
}
