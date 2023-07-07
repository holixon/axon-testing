package io.holixon.axon.testing.upcaster

import io.holixon.axon.testing.upcaster.UpcasterTest.Companion.DEFAULT_FILE_ENDING
import io.holixon.axon.testing.upcaster.file.getFiles
import org.axonframework.serialization.Serializer
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
  private lateinit var sourceMessageFileEnding: String
  private lateinit var resultMessageFileSuffix: String


  private lateinit var payloadTypeAndRevisionProvider: PayloadTypeAndRevisionProvider
  private lateinit var contentProvider: MessageContentProvider
  private lateinit var serializer: Serializer

  override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
    // make sure provider is initialized correctly.
    sanitizeInitialization()

    // retrieve the test file folder
    val folderName = context.getTestFolderName()
    val folder = notNull(
      context.getTestFolder(),
      "Could not access the test data folder '$folderName', please create it in your test resources folder."
    )!!

    val resultMessageFileEnding = resultMessageFileSuffix + sourceMessageFileEnding
    // retrieve files
    val testSourceFiles = folder.getFiles(sourceMessageFileEnding, resultMessageFileSuffix)
    val testResultFiles = folder.getFiles(resultMessageFileEnding)
    condition(
      testSourceFiles.isNotEmpty(),
      "Could not load any test data files (*.$sourceMessageFileEnding) from provided folder $folderName."
    )


    // find payload type and revision provider
    payloadTypeAndRevisionProvider =
      notNull(
        context.getPayloadTypeAndRevisionProvider(payloadTypeAndRevisionProviderClazz),
        "Payload type and revision provider could not be initialized. You configured ${payloadTypeAndRevisionProviderClazz.simpleName}."
      )!!

    // content provider
    contentProvider =
      notNull(
        context.getMessageContentProvider(contentProviderClazz, messageEncoding),
        "Message content provider could not be initialized. You configured ${contentProviderClazz.simpleName} and message encoding $messageEncoding."
      )!!

    // find serializer
    serializer = notNull(
      context.getSerializer(this.messageEncoding),
      "Could not find serializer for $messageEncoding. Please provide is as a static member variable of the test."
    )!!

    val relevantParameterCount = context.retrieveRelevantParameterCount()
    condition(
      relevantParameterCount > 0,
      "At least one List<> parameter is required for method annotated with @UpcasterTest." +
        "Currently, the provider supports List<String>, List<File> or List<IntermediateEventRepresentation>."
    )

    // guess the type
    val sourceParameterType = context.detectSourceParameterType() ?: IntermediateEventRepresentation::class.java
    val resultParameterType = if (relevantParameterCount > 1) {
      context.detectTargetParameterType() ?: IntermediateEventRepresentation::class.java
    } else {
      null
    }

    val sources = generateOutput(folderName, sourceMessageFileEnding, sourceParameterType, testSourceFiles)
      ?: throw PreconditionViolationException("Unsupported parameter type: $sourceParameterType")
    val results = generateOutput(folderName, resultMessageFileEnding, resultParameterType, testResultFiles)
    // sanity
    if (resultParameterType == null && testResultFiles.isNotEmpty()) {
      context.publishReportEntry("Warning: detected ${testSourceFiles.size} result files, but could not detect a type of second method parameter to provide it to the test method.")
    }

    return if (relevantParameterCount > 1) {
      Stream.of(Arguments.of(sources, results))
    } else {
      Stream.of(Arguments.of(sources))
    }
  }


  override fun accept(annotation: UpcasterTest) {
    this.messageEncoding = annotation.messageEncoding
    this.payloadTypeAndRevisionProviderClazz = annotation.payloadTypeProvider
    this.contentProviderClazz = annotation.messageContentProvider
    this.sourceMessageFileEnding = if (annotation.messageFileEnding == DEFAULT_FILE_ENDING) {
      this.messageEncoding.defaultFileEnding()
    } else {
      annotation.messageFileEnding
    }
    this.resultMessageFileSuffix = annotation.resultMessageFileSuffix
  }

  private fun sanitizeInitialization() {
    condition(this::messageEncoding.isInitialized, "Event encoding must be initialized.")
    condition(this::payloadTypeAndRevisionProviderClazz.isInitialized, "Payload Type and Revision provider must be initialized.");
    condition(this::contentProviderClazz.isInitialized, "Message content provider must be initialized.");
    condition(this::sourceMessageFileEnding.isInitialized, "Message file ending must be initialized.");
    condition(this::resultMessageFileSuffix.isInitialized, "Result message file suffix must be initialized.");
  }

  private fun generateOutput(
    folderName: String,
    fileEnding: String,
    parameterType: Class<out Any>?,
    files: List<File>,
  ) = when (parameterType) {
    // events
    IntermediateEventRepresentation::class.java -> {
      NamedList(
        name = "*.$fileEnding events from $folderName",
        payload = files
          .map { file ->
            Payload(
              payloadType = payloadTypeAndRevisionProvider.getPayloadType(fileEnding, file),
              revision = payloadTypeAndRevisionProvider.getRevision(fileEnding, file),
              content = contentProvider.getMessagePayload(file, messageEncoding),
              metadata = contentProvider.getMessageMetadata(file, messageEncoding)
            )
          }.map {
            InitialEventRepresentation(it.constructEntry(messageEncoding), serializer)
          }
      )
    }
    // files
    File::class.java -> {
      NamedList(
        name = "*.$fileEnding files from $folderName",
        payload = files
      )
    }
    // file content
    String::class.java -> {
      NamedList(
        name = "*.$fileEnding file content from $folderName",
        payload = files.map { file ->
          file.readText().trim()
        }
      )
    }
    else -> null
  }


  /**
   * Named list used for nicer display.
   */
  class NamedList<T : Any>(private val name: String, private val payload: List<T>) : List<T> by payload, Named<List<T>> {
    override fun getName(): String = name
    override fun getPayload(): List<T> = payload
  }
}

