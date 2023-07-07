package io.holixon.axon.testing.upcaster.content

import io.holixon.axon.testing.upcaster.MessageContentProvider
import io.holixon.axon.testing.upcaster.MessageEncoding
import java.io.File

class ConstantMetadataMessageContentProvider(
  private val fixedMetaData: Any
) : MessageContentProvider {
  override fun getMessagePayload(file: File, messageEncoding: MessageEncoding): Any {
    throw UnsupportedOperationException("Metadata message content provider can't be used to extract message content.")
  }

  override fun getMessageMetadata(file: File, messageEncoding: MessageEncoding): Any {
    return fixedMetaData
  }
}
