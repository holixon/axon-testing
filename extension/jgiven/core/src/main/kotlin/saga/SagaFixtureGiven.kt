@file:Suppress("unused")

package io.holixon.axon.testing.jgiven.saga

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.*
import io.holixon.axon.testing.jgiven.AxonJGivenStage
import org.axonframework.modelling.saga.repository.inmemory.InMemorySagaStore
import org.axonframework.test.saga.FixtureExecutionResult
import org.axonframework.test.saga.FixtureExecutionResultImpl
import org.axonframework.test.saga.SagaTestFixture
import org.axonframework.test.saga.WhenState

/**
 * Given stage for saga fixture.
 * @param T saga type.
 */
@AxonJGivenStage
class SagaFixtureGiven<T> : Stage<SagaFixtureGiven<T>>() {

  @ExpectedScenarioState(required = true)
  private lateinit var fixture: SagaTestFixture<T>

  @ProvidedScenarioState
  private lateinit var whenState: WhenState

  @ProvidedScenarioState
  private lateinit var thenState: FixtureExecutionResult

  @ProvidedScenarioState
  private lateinit var sagaStore: InMemorySagaStore

  @ProvidedScenarioState
  private lateinit var sagaType: Class<T>


  @BeforeStage
  internal fun init() {
    thenState = fixture.fixtureExecutionResult
    sagaStore = fixture.sagaStore
    sagaType = fixture.sagaType
  }

  /**
   * Nothing happens before.
   */
  @As("no prior activity")
  fun noPriorActivity(): SagaFixtureGiven<T> = self().apply {
    whenState = fixture.givenNoPriorActivity()
  }

  /**
   * An aggregate published an event.
   * @param aggregateIdentifier aggregate identifier.
   * @param event published event.
   */
  @As("aggregate $ published $")
  fun aggregatePublishedEvent(@Quoted aggregateIdentifier: String, event: Any) = aggregatePublishedEvents(aggregateIdentifier, event)

  /**
   * An aggregate published one or multiple events.
   * @param aggregateIdentifier aggregate identifier.
   * @param events published events.
   */
  @As("aggregate $ published $")
  fun aggregatePublishedEvents(@Quoted aggregateIdentifier: String, vararg events: Any) = aggregatePublishedEvents(aggregateIdentifier, events.toList())

  /**
   * An aggregate published one or multiple events.
   * @param aggregateIdentifier aggregate identifier.
   * @param events published events.
   */
  @As("aggregate $ published $")
  fun aggregatePublishedEvents(@Quoted aggregateIdentifier: String, events: List<Any>) = self().apply {
    whenState = fixture.givenAggregate(aggregateIdentifier).published(*events.toTypedArray())
  }


}
