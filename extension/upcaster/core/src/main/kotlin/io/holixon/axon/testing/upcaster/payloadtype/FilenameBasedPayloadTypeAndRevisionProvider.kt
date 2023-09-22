package io.holixon.axon.testing.upcaster.payloadtype

import io.holixon.axon.testing.upcaster.PayloadTypeAndRevisionProvider
import io.holixon.axon.testing.upcaster.file.extractEffectiveName
import java.io.File

/**
 * Detect payload type and revision based on filename of the event.
 */
class FilenameBasedPayloadTypeAndRevisionProvider : PayloadTypeAndRevisionProvider {

  companion object {
    val REGEX = Regex("__")
  }

  override fun getPayloadType(ending: String, file: File): String {
    val name = file.extractEffectiveName(ending)
    return name.split(REGEX)[0]
  }

  override fun getRevision(ending: String, file: File): String? {
    val name = file.extractEffectiveName(ending)
    return name.split(REGEX).getOrNull(1)
  }
}
