package io.holixon.axon.testing.upcaster

import io.holixon.axon.testing.upcaster.content.StaticTestInstanceMessageContentProvider
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
  val payloadTypeProvider: KClass<out PayloadTypeAndRevisionProvider> = StaticTestInstancePayloadTypeAndRevisionProvider::class,
  val messageContentProvider: KClass<out MessageContentProvider> = StaticTestInstanceMessageContentProvider::class,
  val messageFileEnding: String = DEFAULT_FILE_ENDING
) {
  companion object {
    const val DEFAULT_FILE_ENDING = "__DEFAULT"
  }
}
