package io.holixon.axon.testing.examples.jgiven.junit4.kotlin

import com.tngtech.jgiven.annotation.ProvidedScenarioState
import fixture.bankaccount.BankAccountAggregate
import fixture.bankaccount.BankAccountAggregate.Configuration.DEFAULT_MAXIMAL_BALANCE
import fixture.bankaccount.command.CreateAccountCommand
import fixture.bankaccount.event.AccountCreatedEvent
import io.holixon.axon.testing.jgiven.junit.AggregateFixtureScenarioTest
import io.toolisticon.testing.jgiven.AND
import io.toolisticon.testing.jgiven.GIVEN
import io.toolisticon.testing.jgiven.THEN
import io.toolisticon.testing.jgiven.WHEN
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Test


class BankAccountAggregateJgivenKotlinTest : AggregateFixtureScenarioTest<BankAccountAggregate>() {

  @ProvidedScenarioState
  private val fixture = AggregateTestFixture(BankAccountAggregate::class.java)

  @Test
  fun `create account`() {
    GIVEN
      .noPriorActivity()

    WHEN
      .command(CreateAccountCommand.builder()
        .accountId("1")
        .customerId("1")
        .initialBalance(100)
        .build()
      )

    THEN
      .expectEvent(AccountCreatedEvent.builder()
        .accountId("1")
        .customerId("1")
        .initialBalance(100)
        .maximalBalance(DEFAULT_MAXIMAL_BALANCE)
        .build())
      .AND
      .expectState(BankAccountAggregate.builder()
        .accountId("1")
        .currentBalance(100)
        .maximalBalance(DEFAULT_MAXIMAL_BALANCE)
        .build())
  }
}
