package io.holixon.axon.testing.upcaster

import org.axonframework.eventhandling.EventData
import org.axonframework.messaging.MetaData
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.SimpleSerializedObject
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.InitialEventRepresentation
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.dom4j.Document
import org.dom4j.DocumentHelper
import org.dom4j.io.SAXReader
import java.io.StringReader
import java.util.stream.Stream

/**
 * Helper functions for upcaster tests.
 */
class UpcasterTestSupport {
  companion object {

    /**
     * Empty JSON.
     */
    const val EMPTY_JSON = "{}"

    /**
     * Creates a stream containing a specified event as initial representation.
     * @param entry event serialized as data entry
     *
     */
    @JvmStatic
    fun <T : Any> initialEvent(entry: EventData<T>, serializer: Serializer): Stream<IntermediateEventRepresentation> = Stream.of(InitialEventRepresentation(entry, serializer))


    /**
     * Creates an event data object serialized as JSON.
     * @param payloadJson JSON payload as string.
     * @param metaDataJson JSON metadata as string.
     * @param payloadTypeName payload type name.
     * @param revisionNumber event revision.
     */
    @JvmStatic
    fun jsonTestEventData(payloadJson: String, payloadTypeName: String, metaDataJson: String = EMPTY_JSON, revisionNumber: String? = null): EventData<String> =
      testEventData(payloadJson, payloadTypeName, metaDataJson, revisionNumber)

    /**
     * Creates an event data object serialized as XML.
     * @param payloadDocument XML payload as Document.
     * @param metaDataDocument XML metadata as Document.
     * @param payloadTypeName payload type name.
     * @param revisionNumber event revision.
     */
    @JvmStatic
    fun xmlTestEventData(
      payloadDocument: Document,
      payloadTypeName: String,
      metaDataDocument: Document = DocumentHelper.createDocument(),
      revisionNumber: String? = null
    ): EventData<Document> = testEventData(payloadDocument, payloadTypeName, metaDataDocument, revisionNumber)

    /**
     * Creates an event data object serialized as XML.
     * @param payloadDocument XML payload as String.
     * @param metaDataDocument XML metadata as String.
     * @param payloadTypeName payload type name.
     * @param revisionNumber event revision.
     */
    @JvmStatic
    fun xmlTestEventData(
      payloadDocument: String,
      payloadTypeName: String,
      revisionNumber: String? = null
    ): EventData<Document> = xmlTestEventData(
      payloadDocument = SAXReader().read(StringReader(payloadDocument)),
      payloadTypeName = payloadTypeName,
      metaDataDocument = DocumentHelper.createDocument(),
      revisionNumber
    )

    /**
     * Creates an event data object serialized as JSON.
     * @param payload XML payload as Document.
     * @param metaData XML metadata as Document.
     * @param payloadTypeName payload type name.
     * @param revisionNumber event revision.
     */
    @JvmStatic
    inline fun <reified T : Any> testEventData(
      payload: T,
      payloadTypeName: String,
      metaData: T,
      revisionNumber: String? = null
    ): EventData<T> =
      TestEventData(
        metaData = serializedObject(payload = metaData, payloadTypeName = MetaData::class.java.name, revisionNumber = revisionNumber),
        payload = serializedObject(payload = payload, payloadTypeName = payloadTypeName, revisionNumber = revisionNumber)
      )

    /**
     * Reified version of factory method.
     */
    inline fun <reified T> serializedObject(payload: T, payloadTypeName: String, revisionNumber: String?) =
      SimpleSerializedObject(payload, T::class.java, SimpleSerializedType(payloadTypeName, revisionNumber))
  }

}
