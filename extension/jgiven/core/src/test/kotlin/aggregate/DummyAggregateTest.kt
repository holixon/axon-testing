package io.holixon.axon.testing.jgiven.aggregate

import com.tngtech.jgiven.annotation.ProvidedScenarioState
import io.holixon.axon.testing.jgiven.AxonJGiven.aggregateTestFixtureBuilder
import io.holixon.axon.testing.jgiven.AxonJGivenTestFixtures
import io.holixon.axon.testing.jgiven.AxonJGivenTestFixtures.CreateDummyAggregate
import io.holixon.axon.testing.jgiven.AxonJGivenTestFixtures.DummyAggregate
import io.holixon.axon.testing.jgiven.AxonJGivenTestFixtures.DummyAggregateCreated
import io.toolisticon.testing.jgiven.GIVEN
import io.toolisticon.testing.jgiven.THEN
import io.toolisticon.testing.jgiven.WHEN
import org.junit.jupiter.api.Test

class DummyAggregateTest : AxonJGivenTestFixtures.AggregateFixtureScenarioTest<DummyAggregate>() {

  @ProvidedScenarioState
  val fixture = aggregateTestFixtureBuilder<DummyAggregate>()
    .build()

  @Test
  fun `create aggregate`() {
    GIVEN
      .noPriorActivity()

    WHEN
      .command(CreateDummyAggregate("1"))

    THEN
      .expectEvent(DummyAggregateCreated("1"))
  }

  @Test
  fun `just no events`() {
    GIVEN
      .noPriorActivity()

    THEN
      .expectNoEvents()
  }
}
