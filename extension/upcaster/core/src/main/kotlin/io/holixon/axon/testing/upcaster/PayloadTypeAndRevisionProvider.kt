package io.holixon.axon.testing.upcaster

import java.io.File

interface PayloadTypeAndRevisionProvider {
  fun getPayloadType(ending: String, file: File): String
  fun getRevision(ending: String, file: File): String?
}
