package io.holixon.axon.testing.upcaster

/**
 * PayloadTypeAndRevisionProvider returning fixed values.
 */
class FixedPayloadTypeAndRevisionProvider(
  private val payloadTypeName: String,
  private val payloadRevision: String?
) : PayloadTypeAndRevisionProvider {
  override fun getPayloadType(payload: Any): String = payloadTypeName
  override fun getRevision(payload: Any): String? = payloadRevision
}
