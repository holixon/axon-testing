package io.holixon.axon.testing.upcaster

import org.axonframework.eventhandling.EventData
import org.dom4j.Document

data class Payload(
  val payloadType: String,
  val revision: String? = null,
  val content: Any,
  val metadata: Any
) {
  fun constructEntry(encoding: MessageEncoding): EventData<*> {
    return when (encoding) {
      MessageEncoding.XSTREAM ->
        UpcasterTestSupport.xmlTestEventData(
          payloadDocument = content.let {
            require(it is Document) { "Content for $encoding must be a dom4j document" }
            it
          },
          payloadTypeName = payloadType,
          metaDataDocument = metadata.let {
            require(it is Document) { "Metadata for $encoding must be a dom4j document" }
            it
          },
          revisionNumber = revision
        )

      MessageEncoding.JACKSON -> UpcasterTestSupport.jsonTestEventData(
        payloadJson = content.toString(),
        payloadTypeName = payloadType,
        metaDataJson = metadata.toString(),
        revisionNumber = revision
      )

      else -> throw IllegalArgumentException("Unsupported encoding $encoding")
    }
  }
}
