package io.holixon.axon.test.jgiven.itest

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.AfterScenario
import com.tngtech.jgiven.annotation.BeforeScenario
import com.tngtech.jgiven.annotation.ExpectedScenarioState
import com.tngtech.jgiven.annotation.ProvidedScenarioState
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.Configuration
import org.axonframework.config.Configurer
import org.axonframework.config.DefaultConfigurer
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.queryhandling.SimpleQueryBus
import kotlin.reflect.KClass

/**
 * Axon integration test stage working with command and event gateways.
 */
open class AxonITestStage<SELF : AxonITestStage<SELF>> : Stage<SELF>() {

  @ExpectedScenarioState
  lateinit var commandGateway: CommandGateway

  @ExpectedScenarioState
  lateinit var queryGateway: QueryGateway

  open fun <T> send(command: Any): T = commandGateway.sendAndWait(command)

  open fun <T : Any> queryOne(query: Any, clazz: KClass<T>): T = queryGateway.query(query, ResponseTypes.instanceOf(clazz.java)).join()

  open fun <T : Any> queryMany(query: Any, clazz: KClass<T>): List<T> = queryGateway.query(query, ResponseTypes.multipleInstancesOf(clazz.java)).join()

  protected val configurer: Configurer = DefaultConfigurer
    .defaultConfiguration()
    .configureEmbeddedEventStore { InMemoryEventStorageEngine() }
    .configureCommandBus { SimpleCommandBus.builder().build() }
    .configureQueryBus { SimpleQueryBus.builder().build() }

  @ProvidedScenarioState
  lateinit var axonConfiguration: Configuration

  @BeforeScenario
  fun initializeAxon() {
    registerAxonComponents()
    this.axonConfiguration = configurer.start()
    this.commandGateway = axonConfiguration.commandGateway()
    this.queryGateway = axonConfiguration.queryGateway()
  }

  @AfterScenario
  fun shutdownAxon() {
    if (this::axonConfiguration.isInitialized) {
      this.axonConfiguration.shutdown()
    }
  }

  /**
   * Method to hook the components to initialize.
   */
  open fun registerAxonComponents() {

  }
}
