package io.holixon.axon.test.jgiven

import com.tngtech.jgiven.base.ScenarioTestBase
import io.holixon.axon.test.jgiven.aggregate.AggregateFixtureGiven
import io.holixon.axon.test.jgiven.aggregate.AggregateFixtureThen
import io.holixon.axon.test.jgiven.aggregate.AggregateFixtureWhen
import io.holixon.axon.test.jgiven.saga.SagaFixtureGiven
import io.holixon.axon.test.jgiven.saga.SagaFixtureThen
import io.holixon.axon.test.jgiven.saga.SagaFixtureWhen

/**
 * Base class for scenario aggregate tests.
 */
abstract class AggregateFixtureScenarioTestBase<T> : ScenarioTestBase<AggregateFixtureGiven<T>, AggregateFixtureWhen<T>, AggregateFixtureThen<T>>()

/**
 * Base class for scenario saga tests.
 */
abstract class SagaFixtureScenarioTestBase<T> : ScenarioTestBase<SagaFixtureGiven<T>, SagaFixtureWhen<T>, SagaFixtureThen<T>>()
