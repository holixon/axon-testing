package io.holixon.axon.testing.upcaster.content

import io.holixon.axon.testing.upcaster.MessageContentProvider
import io.holixon.axon.testing.upcaster.MessageEncoding
import io.holixon.axon.testing.upcaster.UpcasterTestSupport
import org.dom4j.io.SAXReader
import java.io.File
import java.io.StringReader

/**
 * Returns content as string.
 */
class DefaultStringMessageContentProvider : MessageContentProvider {

  override fun getMessagePayload(file: File, messageEncoding: MessageEncoding): Any {
    val contentAsString = file.readText().trim()
    return when (messageEncoding) {
      MessageEncoding.JACKSON -> contentAsString
      MessageEncoding.XSTREAM -> SAXReader().read(StringReader(contentAsString))
      else -> throw IllegalArgumentException("Unsupported encoding format $messageEncoding")
    }
  }

  override fun getMessageMetadata(file: File, messageEncoding: MessageEncoding): Any {
    return when (messageEncoding) {
      MessageEncoding.JACKSON -> UpcasterTestSupport.EMPTY_JSON
      MessageEncoding.XSTREAM -> UpcasterTestSupport.EMPTY_XML
      else -> throw IllegalArgumentException("Unsupported encoding format $messageEncoding")
    }
  }
}
