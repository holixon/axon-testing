package io.holixon.axon.testing.jgiven

import com.tngtech.jgiven.base.ScenarioTestBase
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureGiven
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureThen
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureWhen
import io.holixon.axon.testing.jgiven.aggregate.AggregateTestFixtureBuilder
import io.holixon.axon.testing.jgiven.saga.SagaFixtureGiven
import io.holixon.axon.testing.jgiven.saga.SagaFixtureThen
import io.holixon.axon.testing.jgiven.saga.SagaFixtureWhen
import io.holixon.axon.testing.jgiven.saga.SagaTestFixtureBuilder
import org.axonframework.eventhandling.DomainEventMessage
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.function.Predicate

/**
 * Base class for scenario aggregate tests.
 */
@Suppress("UNUSED")
abstract class AggregateFixtureScenarioTestBase<T> : ScenarioTestBase<AggregateFixtureGiven<T>, AggregateFixtureWhen<T>, AggregateFixtureThen<T>>()

/**
 * Base class for scenario saga tests.
 */
@Suppress("UNUSED")
abstract class SagaFixtureScenarioTestBase<T> : ScenarioTestBase<SagaFixtureGiven<T>, SagaFixtureWhen<T>, SagaFixtureThen<T>>()

object AxonJGiven {

  inline fun <reified T : Any> aggregateTestFixtureBuilder() = AggregateTestFixtureBuilder(T::class.java)
  inline fun <reified T : Any> sagaTestFixtureBuilder() = SagaTestFixtureBuilder(T::class.java)


  internal fun Class<*>.getDeclaredAccessibleField(fieldName: String): Field = getDeclaredField(fieldName)
    .apply {
      isAccessible = true
    }

  internal fun Class<*>.getDeclaredAccessibleMethod(methodName: String): Method = getDeclaredMethod(methodName)
    .apply {
      isAccessible = true
    }

  fun listContainsEventPayloadAndMetaData(payload: Any, metaData: Map<String, Any>): Predicate<List<DomainEventMessage<*>>> = Predicate { list ->
    val msgMeta = list.find { it.payload == payload }?.metaData ?: emptyMap()

    // and if so, does it contain all required metaData?
    metaData.entries.map { (k, v) ->
      msgMeta.containsKey(k) && v == msgMeta[k]
    }.reduce { acc, b -> acc && b }
  }

}

/**
 * Use this when implementing in java
 */
object AxonJGivenJava {

  @JvmStatic
  fun <T : Any> aggregateTestFixtureBuilder(aggregateType: Class<T>) = AggregateTestFixtureBuilder(aggregateType)

  @JvmStatic
  fun <T : Any> sagaTestFixtureBuilder(aggregateType: Class<T>) = SagaTestFixtureBuilder(aggregateType)

}
