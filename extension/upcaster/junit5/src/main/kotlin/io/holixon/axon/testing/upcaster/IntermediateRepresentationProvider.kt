package io.holixon.axon.testing.upcaster

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.support.AnnotationConsumer
import java.util.stream.Stream

class IntermediateRepresentationProvider : ArgumentsProvider, AnnotationConsumer<UpcasterTest> {

  private lateinit var eventEncoding: UpcasterTest.EventEncoding

  override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {

    sanitize()
    val folderName = context.getTestFolderName();
    val folder = requireNotNull(context.getTestFolder()) { "Could not access the test data folder $folderName, consider defining it" }

    val ending = when (eventEncoding) {
      UpcasterTest.EventEncoding.XML -> "xml"
      UpcasterTest.EventEncoding.JSON -> "json"
      UpcasterTest.EventEncoding.AVRO -> "avro"
    }
    val testFiles = folder.getFiles(ending)
    require(testFiles.isNotEmpty()) { "Could not load any test data files (*.$eventEncoding) from provided folder $folderName" }
    return Stream.of(

    )
  }

  override fun accept(annotation: UpcasterTest) {
    this.eventEncoding = annotation.eventEncoding
  }

  private fun sanitize() {
    require(this::eventEncoding.isInitialized) { "Event encoding must be initialized to use this provider." }
  }
}
