@file:Suppress("unused")
package io.holixon.axon.test.jgiven.saga

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.*
import io.holixon.axon.test.jgiven.AxonJGivenStage
import org.axonframework.test.saga.FixtureExecutionResult
import org.axonframework.test.saga.SagaTestFixture
import org.axonframework.test.saga.WhenState

@AxonJGivenStage
class SagaFixtureGiven<T> : Stage<SagaFixtureGiven<T>>() {

  @ExpectedScenarioState(required = true)
  lateinit var fixture: SagaTestFixture<T>

  @ProvidedScenarioState
  lateinit var whenState: WhenState

  @ProvidedScenarioState
  lateinit var thenState: FixtureExecutionResult

  @BeforeStage
  fun init() {
    with(SagaTestFixture::class.java.getDeclaredField("fixtureExecutionResult")) {
      isAccessible = true
      thenState = get(fixture) as FixtureExecutionResult
    }
  }


  @As("no prior activity")
  fun noPriorActivity(): SagaFixtureGiven<T> = self().apply {
    whenState = fixture.givenNoPriorActivity()
  }

  @As("aggregate $ published $")
  fun aggregatePublishedEvent(@Quoted aggregateIdentifier: String, event: Any) = aggregatePublishedEvents(aggregateIdentifier, event)

  @As("aggregate $ published $")
  fun aggregatePublishedEvents(@Quoted aggregateIdentifier: String, @Table vararg events: Any) = aggregatePublishedEvents(aggregateIdentifier, events.toList())

  @As("aggregate $ published $")
  fun aggregatePublishedEvents(@Quoted aggregateIdentifier: String, events: List<Any>) = self().apply {
    whenState = fixture.givenAggregate(aggregateIdentifier).published(*events.toTypedArray())
  }


}
