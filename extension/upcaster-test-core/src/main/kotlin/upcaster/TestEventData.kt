package io.holixon.axon.testing.upcaster

import org.axonframework.eventhandling.EventData
import org.axonframework.serialization.SerializedObject
import java.time.Instant
import java.util.*

/**
 * Test event data implementation.
 */
class TestEventData<T : Any>(
  private val eventIdentifier: String = UUID.randomUUID().toString(),
  private val timestamp: Instant = Instant.now(),
  private val metaData: SerializedObject<T>,
  private val payload: SerializedObject<T>
) : EventData<T> {
  override fun getEventIdentifier(): String = eventIdentifier
  override fun getTimestamp(): Instant = timestamp
  override fun getPayload(): SerializedObject<T> = payload
  override fun getMetaData(): SerializedObject<T> = metaData
}
