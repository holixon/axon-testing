package io.holixon.axon.testing.assert

import org.assertj.core.api.AbstractListAssert
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation

class IntermediateEventRepresentationListAssert(
  actual: List<IntermediateEventRepresentation>,
  private val serializer: Serializer
) : AbstractListAssert
<IntermediateEventRepresentationListAssert, List<IntermediateEventRepresentation>, IntermediateEventRepresentation, IntermediateEventRepresentationAssert>(
  actual, IntermediateEventRepresentationListAssert::class.java
) {
  companion object {
    fun assertThat(actual: List<IntermediateEventRepresentation>, serializer: Serializer): IntermediateEventRepresentationListAssert =
      IntermediateEventRepresentationListAssert(actual, serializer)
  }

  override fun toAssert(value: IntermediateEventRepresentation, description: String): IntermediateEventRepresentationAssert =
    IntermediateEventRepresentationAssert(value, serializer).`as`(description)


  override fun newAbstractIterableAssert(iterable: MutableIterable<IntermediateEventRepresentation>): IntermediateEventRepresentationListAssert =
    IntermediateEventRepresentationListAssert(iterable.toList(), serializer)

}
