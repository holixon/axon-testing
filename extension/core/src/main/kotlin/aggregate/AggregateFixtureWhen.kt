@file:Suppress("unused")
package io.holixon.axon.test.jgiven.aggregate

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.*
import io.holixon.axon.test.jgiven.AxonJGivenStage
import org.axonframework.test.aggregate.ResultValidator
import org.axonframework.test.aggregate.TestExecutor

@AxonJGivenStage
class AggregateFixtureWhen<T> : Stage<AggregateFixtureWhen<T>>() {

  @ExpectedScenarioState(required = true)
  lateinit var testExecutor: TestExecutor<T>

  @ProvidedScenarioState
  lateinit var resultValidator: ResultValidator<T>

  @As("command:")
  fun command(@Quoted cmd: Any) = execute { testExecutor.`when`(cmd) }

  @As("command: \$cmd, metadata: \$metadata")
  fun command(@Quoted cmd: Any, @Table metadata: Map<String, *>) = execute { testExecutor.`when`(cmd, metadata) }

  private fun execute(block: () -> ResultValidator<T>) = self().apply { resultValidator = block.invoke() }!!

}
