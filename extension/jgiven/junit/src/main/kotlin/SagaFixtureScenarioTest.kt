package io.holixon.axon.testing.jgiven.junit

import com.tngtech.jgiven.junit.ScenarioTest
import io.holixon.axon.testing.jgiven.saga.SagaFixtureGiven
import io.holixon.axon.testing.jgiven.saga.SagaFixtureThen
import io.holixon.axon.testing.jgiven.saga.SagaFixtureWhen

/**
 * Saga fixture scenario test.
 * @param T saga type.
 */
abstract class  SagaFixtureScenarioTest<T> : ScenarioTest<SagaFixtureGiven<T>, SagaFixtureWhen<T>, SagaFixtureThen<T>>()
