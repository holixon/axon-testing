package io.holixon.axon.testing.upcaster

interface PayloadTypeAndRevisionProvider {
  fun getPayloadType(payload: Any): String
  fun getRevision(payload: Any): String?
}
