package io.holixon.axon.testing.upcaster

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ParameterizedTest(name = "Using {arguments}")
@ArgumentsSource(value = IntermediateRepresentationProvider::class)
annotation class UpcasterTest(
  val eventEncoding: EventEncoding = EventEncoding.JSON,
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
