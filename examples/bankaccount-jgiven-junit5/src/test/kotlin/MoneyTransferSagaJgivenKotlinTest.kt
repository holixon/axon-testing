package io.holixon.axon.testing.examples.jgiven.junit5.kotlin

import com.tngtech.jgiven.annotation.ProvidedScenarioState
import fixture.bankaccount.AccountAggregateTestHelper.*
import fixture.bankaccount.MoneyTransferSaga
import fixture.bankaccount.event.MoneyTransferInitializedEvent
import io.holixon.axon.testing.jgiven.junit5.SagaFixtureScenarioTest
import io.toolisticon.testing.jgiven.AND
import io.toolisticon.testing.jgiven.GIVEN
import io.toolisticon.testing.jgiven.THEN
import io.toolisticon.testing.jgiven.WHEN
import org.axonframework.test.saga.SagaTestFixture
import org.junit.jupiter.api.Test

class MoneyTransferSagaJgivenKotlinTest : SagaFixtureScenarioTest<MoneyTransferSaga>() {

  @ProvidedScenarioState
  private val fixture = SagaTestFixture(MoneyTransferSaga::class.java)

  @Test
  internal fun `initialize money transfer`() {
    GIVEN
      .aggregatePublishedEvent(
        ACCOUNT_ID_1, accountCreatedEvent(ACCOUNT_ID_1, CUSTOMER_ID_1).toBuilder()
          .initialBalance(2000).build()
      )
      .AND
      .aggregatePublishedEvent(ACCOUNT_ID_2, accountCreatedEvent(ACCOUNT_ID_2, CUSTOMER_ID_2))
    ;

    WHEN
      .aggregatePublishes(
        ACCOUNT_ID_1, MoneyTransferInitializedEvent.builder()
          .sourceAccountId(ACCOUNT_ID_1)
          .targetAccountId(ACCOUNT_ID_2)
          .transactionId("1")
          .amount(100)
          .build()
      )

    THEN
      .expectActiveSagas(1)

  }
}
