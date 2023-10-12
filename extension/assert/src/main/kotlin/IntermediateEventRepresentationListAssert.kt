package io.holixon.axon.testing.assert

import org.assertj.core.api.AbstractListAssert
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation

class IntermediateEventRepresentationListAssert(
  actual: List<IntermediateEventRepresentation>,
  private val serializer: Serializer,
  private val strictComparison: Boolean = false
) : AbstractListAssert
<IntermediateEventRepresentationListAssert, List<IntermediateEventRepresentation>, IntermediateEventRepresentation, IntermediateEventRepresentationAssert>(
  actual, IntermediateEventRepresentationListAssert::class.java
) {
  companion object {
    /**
     * Creates the assert for given intermediate representation serialized using given serializer.
     * @param actual current intermediate representation.
     * @param serializer serializer in use.
     * @return the asserting anchor for checks.
     */
    @JvmStatic
    fun assertThat(actual: List<IntermediateEventRepresentation>, serializer: Serializer): IntermediateEventRepresentationListAssert =
      IntermediateEventRepresentationListAssert(actual, serializer)

    /**
     * Creates the assert for given intermediate representation serialized using given serializer.
     * @param actual current intermediate representation.
     * @param serializer serializer in use.
     * @param strictComparison if true equality instead of similarity is applied where possible. Defaults to false.
     * @return the asserting anchor for checks.
     */
    @JvmStatic
    fun assertThat(
      actual: List<IntermediateEventRepresentation>,
      serializer: Serializer,
      strictComparison: Boolean
    ): IntermediateEventRepresentationListAssert {
      return IntermediateEventRepresentationListAssert(actual, serializer, strictComparison)
    }
  }

  override fun toAssert(value: IntermediateEventRepresentation, description: String): IntermediateEventRepresentationAssert =
    IntermediateEventRepresentationAssert(value, serializer, strictComparison).`as`(description)


  override fun newAbstractIterableAssert(iterable: MutableIterable<IntermediateEventRepresentation>): IntermediateEventRepresentationListAssert =
    IntermediateEventRepresentationListAssert(iterable.toList(), serializer)

}
