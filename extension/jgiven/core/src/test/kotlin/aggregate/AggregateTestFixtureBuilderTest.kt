package io.holixon.axon.testing.jgiven.aggregate

import com.tngtech.jgiven.annotation.ProvidedScenarioState
import io.holixon.axon.testing.jgiven.AxonJGiven
import io.holixon.axon.testing.jgiven.AxonJGivenTestFixtures.AggregateFixtureScenarioTest
import io.holixon.axon.testing.jgiven.AxonJGivenTestFixtures.CreateDummyAggregate
import io.holixon.axon.testing.jgiven.AxonJGivenTestFixtures.DummyAggregate
import io.holixon.axon.testing.jgiven.AxonJGivenTestFixtures.DummyAggregateCreated
import io.toolisticon.testing.jgiven.GIVEN
import io.toolisticon.testing.jgiven.THEN
import io.toolisticon.testing.jgiven.WHEN
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.messaging.MessageDispatchInterceptor
import org.axonframework.messaging.correlation.SimpleCorrelationDataProvider
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.function.BiFunction

internal class AggregateTestFixtureBuilderTest {

  @Nested
  inner class RegisterCorrelationDataProvider : AggregateFixtureScenarioTest<DummyAggregate>() {

    private fun addFooMetaToEveryCommand(value: String) = MessageDispatchInterceptor<CommandMessage<*>> {
      BiFunction { _, cmd -> cmd.andMetaData(mapOf("foo" to value)) }
    }

    @ProvidedScenarioState
    val fixture = AxonJGiven.aggregateTestFixtureBuilder<DummyAggregate>()
      .registerCommandDispatchInterceptor(addFooMetaToEveryCommand("bar"))
      .registerCorrelationDataProvider(SimpleCorrelationDataProvider("foo"))
      .build()

    @Test
    fun `metaData foo=bar is propagated to event`() {
      GIVEN.noPriorActivity()

      WHEN.command(CreateDummyAggregate("1"))

      THEN
        .expectEventWithMetaData(DummyAggregateCreated("1"), "foo" to "bar")
    }
  }


  @Nested
  inner class EmptyFixture : AggregateFixtureScenarioTest<DummyAggregate>() {

    @ProvidedScenarioState
    val fixture = AxonJGiven.aggregateTestFixtureBuilder<DummyAggregate>().build()

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

}
