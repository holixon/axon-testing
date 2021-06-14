package io.holixon.axon.testing.jgiven.junit

import com.tngtech.jgiven.junit.ScenarioTest
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureGiven
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureThen
import io.holixon.axon.testing.jgiven.aggregate.AggregateFixtureWhen

/**
 * Aggregate fixture scenario test.
 * @param T aggregate type.
 */
abstract class  AggregateFixtureScenarioTest<T> : ScenarioTest<AggregateFixtureGiven<T>, AggregateFixtureWhen<T>, AggregateFixtureThen<T>>()
