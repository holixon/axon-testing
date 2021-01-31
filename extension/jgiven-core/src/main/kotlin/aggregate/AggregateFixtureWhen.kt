@file:Suppress("unused")

package io.holixon.axon.testing.jgiven.aggregate

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.*
import io.holixon.axon.testing.jgiven.AxonJGivenStage
import io.holixon.axon.testing.jgiven.step
import org.axonframework.test.aggregate.ResultValidator
import org.axonframework.test.aggregate.TestExecutor

/**
 * When stage for aggregate fixture.
 * @param T aggregate type.
 */
@AxonJGivenStage
class AggregateFixtureWhen<T> : Stage<AggregateFixtureWhen<T>>() {

  @ExpectedScenarioState(required = true)
  private lateinit var testExecutor: TestExecutor<T>

  @ProvidedScenarioState
  private lateinit var resultValidator: ResultValidator<T>

  /**
   * Dispatches a command.
   *
   * @param cmd command to dispatch.
   */
  @As("command: \$cmd")
  fun command(@Quoted cmd: Any) = execute { testExecutor.`when`(cmd) }

  /**
   * Dispatches a command.
   * @param cmd command to dispatch.
   * @param metadata metadata to include into command message.
   */
  @As("command: \$cmd, metadata: \$metadata")
  fun command(@Quoted cmd: Any, @Table metadata: Map<String, *>) = execute { testExecutor.`when`(cmd, metadata) }

  private fun execute(block: () -> ResultValidator<T>) = step { resultValidator = block.invoke() }

}
