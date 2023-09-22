package io.holixon.axon.testing.upcaster.content

import io.holixon.axon.testing.upcaster.MessageContentProvider
import io.holixon.axon.testing.upcaster.MessageEncoding
import java.io.File

/**
 * Delegates to other message content providers.
 */
class DelegatingMessageContentProvider(
  private val contentMessageContentProvider: MessageContentProvider,
  private val metadataMessageContentProvider: MessageContentProvider
) : MessageContentProvider {
  override fun getMessagePayload(file: File, messageEncoding: MessageEncoding): Any {
    return contentMessageContentProvider.getMessagePayload(file, messageEncoding)
  }

  override fun getMessageMetadata(file: File, messageEncoding: MessageEncoding): Any {
    return metadataMessageContentProvider.getMessageMetadata(file, messageEncoding)
  }
}
