package io.holixon.axon.testing.assert

import org.assertj.core.api.AbstractAssert
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * Asserts intermediate representation.
 */
class IntermediateEventRepresentationStreamAssert(actual: Stream<IntermediateEventRepresentation>, private val serializer: Serializer) :
  AbstractAssert<IntermediateEventRepresentationStreamAssert, Stream<IntermediateEventRepresentation>>(
    actual,
    IntermediateEventRepresentationStreamAssert::class.java
  ) {


  companion object {
    /**
     * Creates the assert for given stream of intermediate representations serialized using given serializer.
     * @param actual current intermediate representation.
     * @param serializer serializer in use.
     * @return the asserting anchor for checks.
     */
    @JvmStatic
    fun assertThat(actual: Stream<IntermediateEventRepresentation>, serializer: Serializer) = IntermediateEventRepresentationStreamAssert(actual, serializer)
  }

  /**
   * Asserts that the deserialized version of data is element-wise equal to deserialized version of given.
   * @param expected intermediate representation.
   * @param T type of the payload.
   */
  fun <T : Any> containsExactlyDeserializedElementsOf(expected: Stream<IntermediateEventRepresentation>, clazz: Class<T>): IntermediateEventRepresentationStreamAssert {
    isNotNull
    val deserialized: List<T> = actual.collect(Collectors.toList()).map { ier -> serializer.deserialize(ier.data) }
    val deserializedExpected: List<T> = expected.collect(Collectors.toList()).map { ier -> serializer.deserialize(ier.data) }
    if (deserialized != deserializedExpected) {
      failWithMessage("Expected the deserialized data to be <%s> but it was <%s>", deserializedExpected, deserialized)
    }
    return this
  }


}
