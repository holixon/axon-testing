package io.holixon.axon.testing.jgiven

import com.tngtech.jgiven.junit5.ScenarioTest
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureGiven
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureThen
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureWhen
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.TargetAggregateIdentifier

object AxonJGivenTestFixtures {

  // copied from `axon-testing-jgiven-junit5` so we can do junit5 tests here in the core module.
  abstract class AggregateFixtureScenarioTest<T> : ScenarioTest<AggregateFixtureGiven<T>, AggregateFixtureWhen<T>, AggregateFixtureThen<T>>()

  data class CreateDummyAggregate(
    @TargetAggregateIdentifier
    val id: String
  )

  data class DummyAggregateCreated(
    val id: String
  )

  class DummyAggregate() {

    companion object {

      @CommandHandler
      @JvmStatic
      fun create(cmd: CreateDummyAggregate) = DummyAggregate().apply {
        AggregateLifecycle.apply(DummyAggregateCreated(cmd.id))
      }

    }

    @AggregateIdentifier
    lateinit var id: String

    @EventSourcingHandler
    fun on(evt: DummyAggregateCreated) {
      this.id = evt.id
    }
  }

}
