package io.holixon.axon.test.jgiven.junit

import com.tngtech.jgiven.junit.ScenarioTest
import io.holixon.axon.test.jgiven.aggregate.AggregateFixtureGiven
import io.holixon.axon.test.jgiven.aggregate.AggregateFixtureThen
import io.holixon.axon.test.jgiven.aggregate.AggregateFixtureWhen

abstract class  AggregateFixtureScenarioTest<T> : ScenarioTest<AggregateFixtureGiven<T>, AggregateFixtureWhen<T>, AggregateFixtureThen<T>>()
