@file:Suppress("unused")

package io.holixon.axon.testing.jgiven.aggregate

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.As
import com.tngtech.jgiven.annotation.ProvidedScenarioState
import com.tngtech.jgiven.annotation.Quoted
import io.holixon.axon.testing.jgiven.AxonJGivenStage
import io.holixon.axon.testing.jgiven.step
import org.axonframework.messaging.MetaData
import org.axonframework.test.aggregate.ResultValidator
import java.time.Duration
import java.time.Instant

/**
 * When stage for aggregate fixture.
 * @param T aggregate type.
 */
@AxonJGivenStage
class AggregateFixtureWhen<T> : Stage<AggregateFixtureWhen<T>>() {

  @ProvidedScenarioState
  private var context: AggregateTestFixtureContext<T> = AggregateTestFixtureContext()

  /**
   * Dispatches a command.
   *
   * @param cmd command to dispatch.
   */
  @As("command: \$cmd")
  fun command(@Quoted cmd: Any): AggregateFixtureWhen<T> = command(cmd, MetaData.emptyInstance())

  /**
   * Dispatches a command.
   * @param cmd command to dispatch.
   * @param metadata metadata to include into command message.
   */
  @As("command: \$cmd, metadata: \$metadata")
  fun command(@Quoted cmd: Any, metadata: Map<String, *>): AggregateFixtureWhen<T> = execute { context.testExecutor!!.`when`(cmd, metadata) }

  /**
   * Moves time to new value.
   * @param instant new time to set.
   */
  fun timeAdvancesTo(instant: Instant) {
    context.testExecutor!!.whenThenTimeAdvancesTo(instant)
  }

  /**
   * Moves time to new value.
   * @param duration timespan to move time to.
   */
  fun timeElapses(duration: Duration) {
    context.testExecutor!!.whenThenTimeElapses(duration)
  }

  private fun execute(block: () -> ResultValidator<T>) = step { context.resultValidator = block.invoke() }

}
