package io.holixon.axon.testing.upcaster

import org.junit.jupiter.api.Named
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.support.AnnotationConsumer
import java.util.stream.Stream

/**
 * Provider for intermediate representation of events read from file system.
 */
class IntermediateRepresentationProvider : ArgumentsProvider, AnnotationConsumer<UpcasterTest> {

  private lateinit var eventEncoding: UpcasterTest.EventEncoding

  override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {

    sanitize()
    val folderName = context.getTestFolderName()
    val folder = requireNotNull(context.getTestFolder()) { "Could not access the test data folder $folderName, consider creating it in your resources folder." }

    val ending = when (eventEncoding) {
      UpcasterTest.EventEncoding.XML -> "xml"
      UpcasterTest.EventEncoding.JSON -> "json"
      UpcasterTest.EventEncoding.AVRO -> "avro"
    }
    val testFiles = folder.getFiles(ending)
    require(testFiles.isNotEmpty()) { "Could not load any test data files (*.$ending) from provided folder $folderName." }
    return Stream.of(
      Arguments.of(
        NamedStream(
          name = "*.$ending files from $folderName",
          stream = testFiles.map { it.readText().trim() }.stream()
        )
      )
    )
  }

  override fun accept(annotation: UpcasterTest) {
    this.eventEncoding = annotation.eventEncoding
  }

  private fun sanitize() {
    require(this::eventEncoding.isInitialized) { "Event encoding must be initialized to use this provider." }
  }

  class NamedStream<T: Any>(private val name: String, private val stream: Stream<T>): Stream<T> by stream, Named<Stream<T>> {
    override fun getName(): String = name
    override fun getPayload(): Stream<T>  = stream
  }
}
