package io.holixon.axon.testing.upcaster

import io.holixon.axon.testing.upcaster.UpcasterTest.Companion.DEFAULT_FILE_ENDING
import io.holixon.axon.testing.upcaster.file.getFiles
import org.axonframework.serialization.upcasting.event.InitialEventRepresentation
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.support.AnnotationConsumer
import org.junit.platform.commons.PreconditionViolationException
import org.junit.platform.commons.util.Preconditions.condition
import org.junit.platform.commons.util.Preconditions.notNull
import java.io.File
import java.util.stream.Stream
import kotlin.reflect.KClass

/**
 * Provider for intermediate representation of events read from file system.
 */
class IntermediateRepresentationProvider : ArgumentsProvider, AnnotationConsumer<UpcasterTest> {

  private lateinit var payloadTypeAndRevisionProviderClazz: KClass<out PayloadTypeAndRevisionProvider>
  private lateinit var contentProviderClazz: KClass<out MessageContentProvider>
  private lateinit var messageEncoding: MessageEncoding
  private lateinit var messageFileEnding: String

  override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
    // make sure provider is initialized correctly.
    sanitize()

    // retrieve the test file folder
    val folderName = context.getTestFolderName()
    val folder = notNull(
      context.getTestFolder(),
      "Could not access the test data folder $folderName, consider creating it in your resources folder."
    )!!

    // retrieve test files
    val ending = this.messageEncoding.defaultFileEnding()
    val testFiles = folder.getFiles(ending)
    condition(
      testFiles.isNotEmpty(),
      "Could not load any test data files (*.$ending) from provided folder $folderName."
    )

    // find payload type and revision provider
    val payloadTypeAndRevisionProvider =
      notNull(
        context.getPayloadTypeAndRevisionProvider(this.payloadTypeAndRevisionProviderClazz),
        "Payload type and revision provider could not be initialized. You configured ${payloadTypeAndRevisionProviderClazz.simpleName}."
      )!!

    // content provider
    val contentProvider =
      notNull(
        context.getMessageContentProvider(this.contentProviderClazz),
        "Message content provider could not be initialized. You configured ${contentProviderClazz.simpleName}."
      )!!

    // find serializer
    val serializer = notNull(
      context.getSerializer(this.messageEncoding),
      "Could not find serializer for $messageEncoding. Please provide is as a static member variable of the test."
    )!!

    // guess the type
    val parameterType = context.detectParameterType() ?: IntermediateEventRepresentation::class.java

    return when (parameterType) {
      // events
      IntermediateEventRepresentation::class.java -> {
        NamedList(
          name = "*.$ending events from $folderName",
          payload = testFiles
            .map { file ->
              Payload(
                payloadType = payloadTypeAndRevisionProvider.getPayloadType(ending, file),
                revision = payloadTypeAndRevisionProvider.getRevision(ending, file),
                content = contentProvider.getMessagePayload(file, this.messageEncoding),
                metadata = contentProvider.getMessageMetadata(file, this.messageEncoding)
              )
            }.map {
              InitialEventRepresentation(it.constructEntry(this.messageEncoding), serializer)
            }
        )
      }
      // files
      File::class.java -> {
        NamedList(
          name = "*.$ending files from $folderName",
          payload = testFiles
        )
      }
      // file content
      String::class.java -> {
        NamedList(
          name = "*.$ending file content from $folderName",
          payload = testFiles.map { file ->
            file.readText().trim()
          }
        )
      }

      else -> {
        throw PreconditionViolationException("Unsupported parameter type: $parameterType")
      }
    }.let {
      Stream.of(Arguments.of(it))
    }
  }


  override fun accept(annotation: UpcasterTest) {
    this.messageEncoding = annotation.messageEncoding
    this.payloadTypeAndRevisionProviderClazz = annotation.payloadTypeProvider
    this.contentProviderClazz = annotation.messageContentProvider
    this.messageFileEnding = if (annotation.messageFileEnding == DEFAULT_FILE_ENDING) {
      this.messageEncoding.defaultFileEnding()
    } else {
      annotation.messageFileEnding
    }
  }

  private fun sanitize() {
    condition(this::messageEncoding.isInitialized, "Event encoding must be initialized.")
    condition(this::payloadTypeAndRevisionProviderClazz.isInitialized, "Payload Type and Revision provider must be initialized.");
    condition(this::contentProviderClazz.isInitialized, "Message content provider must be initialized.");
    condition(this::messageFileEnding.isInitialized, "Message file ending must be initialized.");
  }


  /**
   * Named list.
   */
  class NamedList<T : Any>(private val name: String, private val payload: List<T>) : List<T> by payload, Named<List<T>> {
    override fun getName(): String = name
    override fun getPayload(): List<T> = payload
  }
}

