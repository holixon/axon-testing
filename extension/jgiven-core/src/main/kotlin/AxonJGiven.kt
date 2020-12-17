package io.holixon.axon.testing.jgiven

import com.tngtech.jgiven.base.ScenarioTestBase
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureGiven
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureThen
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureWhen
import io.holixon.axon.testing.jgiven.saga.SagaFixtureGiven
import io.holixon.axon.testing.jgiven.saga.SagaFixtureThen
import io.holixon.axon.testing.jgiven.saga.SagaFixtureWhen

/**
 * Base class for scenario aggregate tests.
 */
abstract class AggregateFixtureScenarioTestBase<T> : ScenarioTestBase<AggregateFixtureGiven<T>, AggregateFixtureWhen<T>, AggregateFixtureThen<T>>()

/**
 * Base class for scenario saga tests.
 */
abstract class SagaFixtureScenarioTestBase<T> : ScenarioTestBase<SagaFixtureGiven<T>, SagaFixtureWhen<T>, SagaFixtureThen<T>>()
