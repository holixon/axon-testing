package io.holixon.axon.testing.upcaster

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ParameterizedTest(name = "Using {arguments}")
@ArgumentsSource(value = IntermediateRepresentationProvider::class)
annotation class UpcasterTest(
  val eventEncoding: EventEncoding = EventEncoding.JSON,
  val payloadTypeProvider: KClass<PayloadTypeAndRevisionProvider>
) {
  /**
   * Type of event
   */
  enum class EventEncoding {
    XML,
    JSON,
    AVRO
  }
}
