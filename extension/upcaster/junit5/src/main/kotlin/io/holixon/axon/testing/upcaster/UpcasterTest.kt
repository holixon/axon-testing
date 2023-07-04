package io.holixon.axon.testing.upcaster

import org.junit.jupiter.params.provider.ArgumentsSource

@MustBeDocumented
@ArgumentsSource(IntermediateRepresentationProvider::class)
@Target(AnnotationTarget.FUNCTION)
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
