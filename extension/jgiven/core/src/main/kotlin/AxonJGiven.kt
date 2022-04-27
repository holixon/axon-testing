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
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.saga.SagaTestFixture
import kotlin.reflect.KClass

/**
 * Base class for scenario aggregate tests.
 */
abstract class AggregateFixtureScenarioTestBase<T> : ScenarioTestBase<AggregateFixtureGiven<T>, AggregateFixtureWhen<T>, AggregateFixtureThen<T>>()

/**
 * Base class for scenario saga tests.
 */
abstract class SagaFixtureScenarioTestBase<T> : ScenarioTestBase<SagaFixtureGiven<T>, SagaFixtureWhen<T>, SagaFixtureThen<T>>()

object AxonJGiven {

  inline fun <reified T : Any> aggregateTestFixtureBuilder() = AggregateTestFixtureBuilder(T::class.java)
  inline fun <reified T : Any> sagaTestFixtureBuilder() = SagaTestFixtureBuilder(T::class.java)

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
