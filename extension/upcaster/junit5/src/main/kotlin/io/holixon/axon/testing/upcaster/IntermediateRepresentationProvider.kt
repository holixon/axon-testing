package io.holixon.axon.testing.upcaster

import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.EMPTY_XML
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.initialEvent
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.jsonTestEventData
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.xmlTestEventData
import org.axonframework.eventhandling.EventData
import org.dom4j.io.SAXReader
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.support.AnnotationConsumer
import java.io.File
import java.io.StringReader
import java.util.stream.Stream
import kotlin.reflect.KClass

/**
 * Provider for intermediate representation of events read from file system.
 */
class IntermediateRepresentationProvider : ArgumentsProvider, AnnotationConsumer<UpcasterTest> {

  private lateinit var payloadTypeAndRevisionProviderClazz: KClass<PayloadTypeAndRevisionProvider>
  private lateinit var eventEncoding: UpcasterTest.EventEncoding

  override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
    // make sure provider is initialized correctly.
    sanitize()

    // retrieve the test file folder
    val folderName = context.getTestFolderName()
    val folder = requireNotNull(context.getTestFolder()) { "Could not access the test data folder $folderName, consider creating it in your resources folder." }

    // retrieve test files
    val ending = this.eventEncoding.encodingEnding()
    val testFiles = folder.getFiles(ending)
    require(testFiles.isNotEmpty()) { "Could not load any test data files (*.$ending) from provided folder $folderName." }

    // find payload type and revision provider
    val payloadTypeAndRevisionProvider =
      requireNotNull(context.getPayloadTypeAndRevisionProvider(this.payloadTypeAndRevisionProviderClazz))

    // FIXME: content provider
    val contentProvider = ContentProvider()

    // find serializer
    val serializer =
      requireNotNull(context.getSerializer(this.eventEncoding)) { "Could not find serializer for $eventEncoding. Consider defining it as a member variable of the test." }

    // construct intermediate representation
    return Stream.of(
      Arguments.of(
        NamedStream(
          name = "*.$ending files from $folderName",
          stream = testFiles.map { file ->
            Payload(
              content = contentProvider.invoke(file),
              payloadType = payloadTypeAndRevisionProvider.getPayloadType(file),
              revision = payloadTypeAndRevisionProvider.getRevision(file),
              metadata = null // FIXME -> support metadata
            )
          }.map {
            initialEvent(
              entry = it.entry(this.eventEncoding),
              serializer = serializer
            )
          }.stream()
        )
      )
    )
  }

  @FunctionalInterface
  class ContentProvider : (File) -> Any {
    override fun invoke(file: File): Any {
      return file.readText().trim()
    }
  }

  class Payload(
    val content: Any,
    val payloadType: String,
    val revision: String? = null,
    val metadata: Any? = null
  ) {
    fun entry(encoding: UpcasterTest.EventEncoding): EventData<*> {
      return when (encoding) {
        UpcasterTest.EventEncoding.XML ->
          xmlTestEventData(
            payloadDocument = SAXReader().read(
              StringReader(this.content as String)
            ),
            payloadTypeName = this.payloadType,
            metaDataDocument = metadata?.let {
              SAXReader().read(
                StringReader(metadata as String)
              )
            } ?: EMPTY_XML,
            revisionNumber = this.revision
          )

        UpcasterTest.EventEncoding.JSON -> jsonTestEventData(
          payloadJson = this.content as String,
          payloadTypeName = this.payloadType,
          metaDataJson = metadata as? String ?: UpcasterTestSupport.EMPTY_JSON,
          revisionNumber = this.revision
        )

        else -> throw IllegalArgumentException("Not implemented for $encoding")
      }
    }

  }

  override fun accept(annotation: UpcasterTest) {
    this.eventEncoding = annotation.eventEncoding
    this.payloadTypeAndRevisionProviderClazz = annotation.payloadTypeProvider
  }

  private fun sanitize() {
    require(this::eventEncoding.isInitialized) { "Event encoding must be initialized to use this provider." }
    require(this::payloadTypeAndRevisionProviderClazz.isInitialized) { "Payload Type and Revision provider must be initialized to use this provider." }
  }

  class NamedStream<T : Any>(private val name: String, private val stream: Stream<T>) : Stream<T> by stream, Named<Stream<T>> {
    override fun getName(): String = name
    override fun getPayload(): Stream<T> = stream
  }

  private fun UpcasterTest.EventEncoding.encodingEnding() =
    when (this) {
      io.holixon.axon.testing.upcaster.UpcasterTest.EventEncoding.XML -> "xml"
      io.holixon.axon.testing.upcaster.UpcasterTest.EventEncoding.JSON -> "json"
      io.holixon.axon.testing.upcaster.UpcasterTest.EventEncoding.AVRO -> "avro"
    }

}
