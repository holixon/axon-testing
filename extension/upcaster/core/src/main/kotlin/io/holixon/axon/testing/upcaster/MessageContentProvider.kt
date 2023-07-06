package io.holixon.axon.testing.upcaster

import java.io.File

/**
 * Reads message file and retrieves the payload and metadata of the message.
 */
interface MessageContentProvider {

  /**
   * Retrieves message payload.
   * @param file message file.
   * @param messageEncoding encoding.
   * @return content.
   */
  fun getMessagePayload(file: File, messageEncoding: MessageEncoding): Any

  /**
   * Retrieves message metadata.
   * @param file message file.
   * @param messageEncoding encoding.
   * @return metadata or null.
   */
  fun getMessageMetadata(file: File, messageEncoding: MessageEncoding): Any
}
