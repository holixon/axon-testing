package io.holixon.axon.testing.examples.jgiven.junit4.kotlin

import com.tngtech.jgiven.annotation.ProvidedScenarioState
import fixture.bankaccount.AccountAggregateTestHelper.*
import fixture.bankaccount.MoneyTransferSaga
import fixture.bankaccount.event.MoneyTransferInitializedEvent
import io.holixon.axon.testing.jgiven.AxonJGiven
import io.holixon.axon.testing.jgiven.AxonJGiven.sagaTestFixtureBuilder
import io.holixon.axon.testing.jgiven.junit.SagaFixtureScenarioTest
import io.toolisticon.testing.jgiven.GIVEN
import io.toolisticon.testing.jgiven.THEN
import io.toolisticon.testing.jgiven.WHEN
import org.junit.Test

class MoneyTransferSagaJgivenKotlinTest : SagaFixtureScenarioTest<MoneyTransferSaga>() {

  @ProvidedScenarioState
  private val fixture = sagaTestFixtureBuilder<MoneyTransferSaga>()
    .registerStartRecordingCallback({})
    .build()

  @Test
  internal fun `initialize money transfer`() {
    GIVEN
      .aggregatePublishedEvent(
        ACCOUNT_ID_1, accountCreatedEvent(ACCOUNT_ID_1, CUSTOMER_ID_1).toBuilder()
          .initialBalance(2000).build()
      )
      .and()
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
