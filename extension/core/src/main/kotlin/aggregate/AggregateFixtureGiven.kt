@file:Suppress("unused")
package io.holixon.axon.test.jgiven.aggregate

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.*
import io.holixon.axon.test.jgiven.AxonJGivenStage
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.TestExecutor
import java.time.Duration
import java.time.Instant
import java.util.function.Supplier


@AxonJGivenStage
class AggregateFixtureGiven<T> : Stage<AggregateFixtureGiven<T>>() {

  @ExpectedScenarioState(required = true)
  lateinit var fixture: AggregateTestFixture<T>

  @ProvidedScenarioState
  lateinit var testExecutor: TestExecutor<T>

  @As("no prior activity")
  fun noPriorActivity(): AggregateFixtureGiven<T> = execute { fixture.givenNoPriorActivity() }

  @As("command:")
  fun command(@Quoted command: Any) = this.commands(command)

  @As("commands:")
  fun commands(@Quoted @Table vararg commands: Any) = this.commands(commands.toList())

  @As("commands:")
  fun commands(@Quoted @Table commands: List<Any>) = execute {
    if (!::testExecutor.isInitialized)
      fixture.givenCommands(commands)
    else
      testExecutor.andGivenCommands(commands)
  }

  @As("event:")
  fun event(@Quoted event: Any) = this.events(event)

  @As("events:")
  fun events(@Quoted @Table vararg events: Any) = this.events(events.toList())

  @As("events:")
  fun events(@Quoted @Table events: List<Any>) = execute {
    if (!::testExecutor.isInitialized)
      fixture.given(events)
    else
      testExecutor.andGiven(events)
  }

  fun currentTime(instant: Instant) = execute {
    if (!::testExecutor.isInitialized)
      fixture.givenCurrentTime(instant)
    else
      testExecutor.andGivenCurrentTime(instant)
  }

  fun state(aggregate: Supplier<T>) = execute {
    fixture.givenState(aggregate)
  }

  fun state(aggregate: () -> T) = execute {
    fixture.givenState(aggregate)
  }

  fun timeAdvancesTo(instant: Instant) {
    if (!::testExecutor.isInitialized)
      fixture.whenThenTimeAdvancesTo(instant)
    else
      testExecutor.whenThenTimeAdvancesTo(instant)
  }

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
