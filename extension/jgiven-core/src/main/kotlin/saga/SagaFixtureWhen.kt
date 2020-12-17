@file:Suppress("unused")
package io.holixon.axon.testing.jgiven.saga

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.As
import com.tngtech.jgiven.annotation.ExpectedScenarioState
import com.tngtech.jgiven.annotation.ProvidedScenarioState
import com.tngtech.jgiven.annotation.Quoted
import io.holixon.axon.testing.jgiven.AxonJGivenStage
import org.axonframework.test.saga.FixtureExecutionResult
import org.axonframework.test.saga.WhenState
import java.time.Duration
import java.time.Instant


/**
 * When stage for saga fixture.
 * @param T aggregate type.
 */
@AxonJGivenStage
class SagaFixtureWhen<T> : Stage<SagaFixtureWhen<T>>() {

  @ExpectedScenarioState(required = true)
  lateinit var whenState: WhenState

  @ProvidedScenarioState
  lateinit var thenState: FixtureExecutionResult

  @As("when aggregate $ publishes event: $")
  fun aggregatePublishes(@Quoted aggregateIdentifier: String, event: Any): SagaFixtureWhen<T> = execute {
    whenState.whenAggregate(aggregateIdentifier).publishes(event)
  }

  /**
   * Time advances.
   * @param instant new time to set.
   */
  fun timeAdvancesTo(instant: Instant) = execute {
    whenState.whenTimeAdvancesTo(instant)
  }

  /**
   * Time advances.
   * @param duration duration to set time to.
   */
  fun timeElapses(duration: Duration) = execute {
    whenState.whenTimeElapses(duration)
  }

  /**
   * Application has published an event.
   * @param event event published by the application
   */
  fun publishing(event: Any): SagaFixtureWhen<T> = execute {
    whenState.whenPublishingA(event)
  }


  private fun execute(block: () -> FixtureExecutionResult) = self().apply {
    thenState = block.invoke()
  }!!

}
