package io.holixon.axon.testing.jgiven.junit5

import com.tngtech.jgiven.annotation.ProvidedScenarioState
import io.holixon.axon.testing.jgiven.AxonJGiven
import io.toolisticon.testing.jgiven.GIVEN
import io.toolisticon.testing.jgiven.THEN
import io.toolisticon.testing.jgiven.WHEN
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.junit.jupiter.api.Test
import java.util.*
import javax.inject.Inject

internal class SagaTestFixtureTest : SagaFixtureScenarioTest<SagaTestFixtureTest.DummySaga>(){

  private val id = UUID.randomUUID().toString()

  @ProvidedScenarioState
  private val fixture = AxonJGiven.sagaTestFixtureBuilder<DummySaga>().build()


  @Test
  fun `expectDispatchedCommands can handle different types - axon default`() {
    GIVEN
      .noPriorActivity()

    WHEN
      .aggregatePublishes(id, DummyEvent(id, "name", "type"))

    THEN
      .expectDispatchedCommands(
        CommandA(id, "name"),
        CommandB(id, "type")
      )
  }

  open class DummySaga {

    @Inject
    @Transient
    private lateinit var commandGateway: CommandGateway


    @SagaEventHandler(associationProperty = "id")
    @StartSaga
    fun onDummyEvent(event: DummyEvent) {

      commandGateway.sendAndWait<Any>(CommandA(event.id, event.name))
      commandGateway.sendAndWait<Any>(CommandB(event.id, event.type))

    }

  }

  data class DummyEvent(val id: String, val name: String, val type: String)
  data class CommandA(val id: String, val name: String)
  data class CommandB(val id: String, val type: String)
}

