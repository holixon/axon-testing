package io.holixon.axon.testing.jgiven

import com.tngtech.jgiven.Stage
import org.axonframework.test.aggregate.ResultValidator

/**
 * Marker annotation to identify Axon JGiven Stages.
 */
annotation class AxonJGivenStage

internal inline fun <X : Stage<X>> Stage<X>.step(block: X.() -> Unit) = self().apply(block)
