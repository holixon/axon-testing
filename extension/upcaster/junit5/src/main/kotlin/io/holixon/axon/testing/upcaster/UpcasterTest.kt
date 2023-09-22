package io.holixon.axon.testing.upcaster

import io.holixon.axon.testing.upcaster.content.MessageEncodingAutoDetectingMessageContentProvider
import io.holixon.axon.testing.upcaster.content.StaticTestInstanceMessageContentProvider
import io.holixon.axon.testing.upcaster.payloadtype.FilenameBasedPayloadTypeAndRevisionProvider
import io.holixon.axon.testing.upcaster.payloadtype.StaticTestInstancePayloadTypeAndRevisionProvider
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ParameterizedTest(name = "Using {arguments}")
@ArgumentsSource(value = IntermediateRepresentationProvider::class)
annotation class UpcasterTest(
  val messageEncoding: MessageEncoding = MessageEncoding.JACKSON,
  val payloadTypeProvider: KClass<out PayloadTypeAndRevisionProvider> = FilenameBasedPayloadTypeAndRevisionProvider::class,
  val messageContentProvider: KClass<out MessageContentProvider> = MessageEncodingAutoDetectingMessageContentProvider::class,
  val messageFileEnding: String = DEFAULT_FILE_ENDING,
  val resultMessageFileSuffix: String = DEFAULT_RESULT_FILE_SUFFIX
) {
  companion object {
    const val DEFAULT_FILE_ENDING = "__DEFAULT"

    /**
     * The suffix expected to be appended to the file name to indicate that the file is a result.
     * For a file '112__my.package.MyType__87.encoding' it will result in
     * '6__my.package.MyType__89__result.encoding'
     */
    const val DEFAULT_RESULT_FILE_SUFFIX = "__result"
  }
}
