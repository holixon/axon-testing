package io.holixon.axon.testing.assert

import org.axonframework.serialization.Serializer
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import java.util.stream.Stream

class AxonAssertions private constructor() {
  companion object {
    /**
     * Creates the assert for given intermediate representation serialized using given serializer.
     * @param actual current intermediate representation.
     * @param serializer serializer in use.
     * @return the asserting anchor for checks.
     */
    @JvmStatic
    fun assertThat(actual: IntermediateEventRepresentation, serializer: Serializer) = IntermediateEventRepresentationAssert.assertThat(actual, serializer)

    /**
     * Creates the assert for given stream of intermediate representations serialized using given serializer.
     * @param actual current intermediate representation.
     * @param serializer serializer in use.
     * @return the asserting anchor for checks.
     */
    @JvmStatic
    fun assertThat(actual: Stream<IntermediateEventRepresentation>, serializer: Serializer) =
      IntermediateEventRepresentationStreamAssert.assertThat(actual, serializer)
  }
}
