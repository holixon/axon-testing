@file:Suppress("unused")

package io.holixon.axon.testing.jgiven.aggregate

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.*
import io.holixon.axon.testing.jgiven.AxonJGivenStage
import io.holixon.axon.testing.jgiven.step
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.TestExecutor
import java.time.Duration
import java.time.Instant
import java.util.function.Supplier

/**
 * Given stage for aggregate fixture.
 * @param T aggregate type.
 */
@AxonJGivenStage
class AggregateFixtureGiven<T> : Stage<AggregateFixtureGiven<T>>() {

  @ExpectedScenarioState(required = true)
  private lateinit var fixture: AggregateTestFixture<T>

  @ProvidedScenarioState
  private val context: AggregateTestFixtureContext<T> = AggregateTestFixtureContext()

  @BeforeStage
  internal fun initStage() {
    context.init(fixture)
  }

  /**
   * Nothing happens before.
   */
  @As("no prior activity")
  fun noPriorActivity(): AggregateFixtureGiven<T> = execute { fixture.givenNoPriorActivity() }

  /**
   * A command has been dispatched.
   * @param command dispatched command.
   */
  @As("command:")
  fun command(@Quoted command: Any): AggregateFixtureGiven<T> = this.commands(command)

  /**
   * One or several commands has been dispatched.
   * @param commands dispatched commands.
   */
  @As("commands:")
  fun commands(@Quoted vararg commands: Any): AggregateFixtureGiven<T> = this.commands(commands.toList())

  /**
   * One or several commands has been dispatched.
   * @param commands dispatched commands.
   */
  @As("commands:")
  fun commands(@Quoted commands: List<Any>): AggregateFixtureGiven<T> = execute {
    context.testExecutor!!.andGivenCommands(commands)
  }

  /**
   * An event has been published.
   * @param event published event.
   */
  @As("event:")
  fun event(@Quoted event: Any): AggregateFixtureGiven<T> = this.events(event)

  /**
   * One or several events has been published.
   * @param events published events.
   */
  @As("events:")
  fun events(@Quoted vararg events: Any): AggregateFixtureGiven<T> = this.events(events.toList())

  /**
   * One or several events has been published.
   * @param events published events.
   */
  @As("events:")
  fun events(@Quoted events: List<Any>): AggregateFixtureGiven<T> = execute {
    context.testExecutor!!.andGiven(events)
  }

  /**
   * Sets the time.
   * @param instant new time.
   */
  fun currentTime(instant: Instant): AggregateFixtureGiven<T> = execute {
    context.testExecutor!!.andGivenCurrentTime(instant)
  }

  /**
   * Sets the state of the aggregate.
   * @param aggregate aggregate state supplier.
   */
  fun state(aggregate: Supplier<T>): AggregateFixtureGiven<T> = execute {
    fixture.givenState(aggregate)
  }

  /**
   * Sets the state of the aggregate.
   * @param aggregate aggregate state providing function..
   */
  fun state(aggregate: () -> T): AggregateFixtureGiven<T> = execute {
    fixture.givenState(aggregate)
  }

  /**
   * Moves time to new value.
   * @param instant new time to set.
   */
  @Deprecated("this belongs in the when stage")
  fun timeAdvancesTo(instant: Instant) {
    context.testExecutor!!.whenThenTimeAdvancesTo(instant)
  }

  /**
   * Moves time to new value.
   * @param duration timespan to move time to.
   */
  @Deprecated("this belongs in the when stage")
  fun timeElapses(duration: Duration) {
    context.testExecutor!!.whenThenTimeElapses(duration)
  }

  private fun execute(block: () -> TestExecutor<T>) = step {
    context.testExecutor = block.invoke()
  }
}
