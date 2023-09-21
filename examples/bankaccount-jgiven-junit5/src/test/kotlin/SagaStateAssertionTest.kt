package io.holixon.axon.testing.examples.jgiven.junit5.kotlin

import com.tngtech.jgiven.annotation.ProvidedScenarioState
import io.holixon.axon.testing.examples.jgiven.junit5.kotlin.SagaStateAssertionTest.StatefulSaga
import io.holixon.axon.testing.examples.jgiven.junit5.kotlin.SagaStateAssertionTest.StatefulSaga.Companion.associationProperty
import io.holixon.axon.testing.jgiven.AxonJGiven
import io.holixon.axon.testing.jgiven.junit5.SagaFixtureScenarioTest
import io.toolisticon.testing.jgiven.AND
import io.toolisticon.testing.jgiven.GIVEN
import io.toolisticon.testing.jgiven.THEN
import io.toolisticon.testing.jgiven.WHEN
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.modelling.saga.AssociationValue
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.junit.jupiter.api.Test

internal class SagaStateAssertionTest : SagaFixtureScenarioTest<StatefulSaga>() {

  data class StartSagaEvent(val key: String)
  data class IncrementSagaEvent(val key: String, val add: Int)
  data class EndSagaEvent(val key: String)

  class StatefulSaga {
    companion object {
      const val ASSOCIATION_PROPERTY = "key"

      fun associationProperty(key: String) = AssociationValue(ASSOCIATION_PROPERTY, key)
    }

    lateinit var key: String
    var state: Int = 0

    @SagaEventHandler(associationProperty = ASSOCIATION_PROPERTY)
    @StartSaga
    fun on(evt: StartSagaEvent) {
      this.key = evt.key
    }

    @SagaEventHandler(associationProperty = ASSOCIATION_PROPERTY)
    fun on(evt: IncrementSagaEvent) {
      this.state += evt.add
    }

    @SagaEventHandler(associationProperty = ASSOCIATION_PROPERTY)
    @EndSaga
    fun on(evt: EndSagaEvent) {
      this.key = evt.key
    }

    override fun toString(): String {
      return "StatefulSaga(key='$key', state=$state)"
    }
  }

  @ProvidedScenarioState
  private val fixture = AxonJGiven.sagaTestFixtureBuilder<StatefulSaga>().build()

  @Test
  fun `a fresh saga has state 0`() {
    val key = "123"

    GIVEN.noPriorActivity()

    WHEN
      .publishing(StartSagaEvent(key))

    THEN
      .expectActiveSagas(1)
      .AND
      .expectSagaState(associationProperty(key), "state is zero") {
        assertThat(it.state).isEqualTo(0)
      }
  }


  @Test
  fun `saga state can be increased`() {
    val key = "234"

    GIVEN.aggregatePublishedEvent(key, StartSagaEvent(key))

    WHEN
      .publishing(IncrementSagaEvent(key, 2))

    THEN
      .expectActiveSagas(1)
      .AND
      .expectSagaState(associationProperty(key), "state is two") {
        assertThat(it.state).isEqualTo(2)
      }
  }

  @Test
  fun `saga can be ended`() {
    val key = "234"

    GIVEN
      .aggregatePublishedEvent(key, StartSagaEvent(key))
      .AND
      .aggregatePublishedEvent(key, IncrementSagaEvent(key, 2))

    WHEN
      .publishing(EndSagaEvent(key))

    THEN
      .expectActiveSagas(0)
  }
}
