@file:Suppress("unused")
package io.holixon.axon.testing.jgiven.aggregate

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.*
import io.holixon.axon.testing.jgiven.AxonJGivenStage
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
  lateinit var fixture: AggregateTestFixture<T>

  @ProvidedScenarioState
  lateinit var testExecutor: TestExecutor<T>

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
  fun command(@Quoted command: Any) = this.commands(command)

  /**
   * One or several commands has been dispatched.
   * @param commands dispatched commands.
   */
  @As("commands:")
  fun commands(@Quoted @Table vararg commands: Any) = this.commands(commands.toList())

  /**
   * One or several commands has been dispatched.
   * @param commands dispatched commands.
   */
  @As("commands:")
  fun commands(@Quoted @Table commands: List<Any>) = execute {
    if (!::testExecutor.isInitialized)
      fixture.givenCommands(commands)
    else
      testExecutor.andGivenCommands(commands)
  }

  /**
   * An event has been published.
   * @param event published event.
   */
  @As("event:")
  fun event(@Quoted event: Any) = this.events(event)

  /**
   * One or several events has been published.
   * @param events published events.
   */
  @As("events:")
  fun events(@Quoted @Table vararg events: Any) = this.events(events.toList())

  /**
   * One or several events has been published.
   * @param events published events.
   */
  @As("events:")
  fun events(@Quoted @Table events: List<Any>) = execute {
    if (!::testExecutor.isInitialized)
      fixture.given(events)
    else
      testExecutor.andGiven(events)
  }

  /**
   * Sets the time.
   * @param instant new time.
   */
  fun currentTime(instant: Instant) = execute {
    if (!::testExecutor.isInitialized)
      fixture.givenCurrentTime(instant)
    else
      testExecutor.andGivenCurrentTime(instant)
  }

  /**
   * Sets the state of the aggregate.
   * @param aggregate aggregate state supplier.
   */
  fun state(aggregate: Supplier<T>) = execute {
    fixture.givenState(aggregate)
  }

  /**
   * Sets the state of the aggregate.
   * @param aggregate aggregate state providing function..
   */
  fun state(aggregate: () -> T) = execute {
    fixture.givenState(aggregate)
  }

  /**
   * Moves time to new value.
   * @param instant new time to set.
   */
  fun timeAdvancesTo(instant: Instant) {
    if (!::testExecutor.isInitialized)
      fixture.whenThenTimeAdvancesTo(instant)
    else
      testExecutor.whenThenTimeAdvancesTo(instant)
  }

  /**
   * Moves time to new value.
   * @param duration timespan to move time to.
   */
  fun timeElapses(duration: Duration) {
    if (!::testExecutor.isInitialized)
      fixture.whenThenTimeElapses(duration)
    else
      testExecutor.whenThenTimeElapses(duration)
  }

  private fun execute(block: () -> TestExecutor<T>) = self().apply {
    testExecutor = block.invoke()
  }!!

}
