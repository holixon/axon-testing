package io.holixon.axon.testing.jgiven.junit

import com.tngtech.jgiven.junit.ScenarioTest
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureGiven
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureThen
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureWhen
import io.holixon.axon.testing.jgiven.saga.SagaFixtureGiven
import io.holixon.axon.testing.jgiven.saga.SagaFixtureThen
import io.holixon.axon.testing.jgiven.saga.SagaFixtureWhen

/**
 * Aggregate fixture scenario test.
 * @param T aggregate type.
 */
abstract class  AggregateFixtureScenarioTest<T> : ScenarioTest<AggregateFixtureGiven<T>, AggregateFixtureWhen<T>, AggregateFixtureThen<T>>()

/**
 * Saga fixture scenario test.
 * @param T saga type.
 */
abstract class  SagaFixtureScenarioTest<T> : ScenarioTest<SagaFixtureGiven<T>, SagaFixtureWhen<T>, SagaFixtureThen<T>>()
