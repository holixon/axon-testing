package io.holixon.axon.testing.upcaster.payloadtype

import io.holixon.axon.testing.upcaster.MessageEncoding
import io.holixon.axon.testing.upcaster.PayloadTypeAndRevisionProvider
import java.io.File

/**
 * PayloadTypeAndRevisionProvider returning fixed values.
 */
class FixedPayloadTypeAndRevisionProvider(
  private val payloadTypeName: String,
  private val payloadRevision: String?
) : PayloadTypeAndRevisionProvider {
  override fun getPayloadType(ending: String, file: File): String = getPayloadType()
  override fun getRevision(ending: String, file: File): String? = getRevision()


  fun getPayloadType(): String = payloadTypeName
  fun getRevision(): String? = payloadRevision
}
