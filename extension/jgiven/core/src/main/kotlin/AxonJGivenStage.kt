package io.holixon.axon.testing.jgiven

import com.tngtech.jgiven.Stage

/**
 * Marker annotation to identify Axon JGiven Stages.
 */
annotation class AxonJGivenStage

/**
 * Step function to create JGiven stage steps easily.
 */
internal inline fun <X : Stage<X>> Stage<X>.step(block: X.() -> Unit) = self().apply(block)
