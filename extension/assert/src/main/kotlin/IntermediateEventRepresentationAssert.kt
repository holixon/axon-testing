package io.holixon.axon.testing.assert

import com.fasterxml.jackson.databind.JsonNode
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.ObjectAssert
import org.axonframework.serialization.SerializedObject
import org.axonframework.serialization.SerializedType
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.xml.XStreamSerializer
import org.dom4j.Document


/**
 * Asserts intermediate representation.
 */
class IntermediateEventRepresentationAssert(
  actual: IntermediateEventRepresentation,
  private val serializer: Serializer,
  private val intermediateRepresentationTypeResolver: (serializer: Serializer) -> Class<*> =
    {
      when (it) {
        is XStreamSerializer -> Document::class.java
        is JacksonSerializer -> JsonNode::class.java
        else -> throw IllegalArgumentException("Unknown serializer type ${serializer::class.java}")
      }
    }
) : AbstractAssert<IntermediateEventRepresentationAssert, IntermediateEventRepresentation>(actual, IntermediateEventRepresentationAssert::class.java) {


  companion object {
    /**
     * Creates the assert for given intermediate representation serialized using given serializer.
     * @param actual current intermediate representation.
     * @param serializer serializer in use.
     * @return the asserting anchor for checks.
     */
    @JvmStatic
    fun assertThat(actual: IntermediateEventRepresentation, serializer: Serializer) = IntermediateEventRepresentationAssert(actual, serializer)
  }

  /**
   * Asserts that the de-serialized data is equals to the given.
   * @param expected expected data.
   * @param T type of the data.
   * @return object assert.
   */
  fun <T : Any> hasDeserializedData(expected: T): ObjectAssert<T> {
    isNotNull
    val intermediateRepresentationType = intermediateRepresentationTypeResolver.invoke(serializer)
    val data = actual.getData(intermediateRepresentationType)
    val event: T = serializer.deserialize(data)
    if (event != expected) {
      failWithMessage("Expected the event to be <%s> but it was <%s>", expected, event)
    }
    return ObjectAssert(event)
  }

  /**
   * Asserts that the serialized data is equals to the given.
   * @param expected expected data.
   * @return object assert.
   */
  fun hasData(expected: SerializedObject<*>): ObjectAssert<SerializedObject<*>> {
    isNotNull
    val so: SerializedObject<*> = actual.data
    if (so != expected) {
      failWithMessage("Expected the serialized object to be <%s> but it was <%s>", expected, so)
    }
    return ObjectAssert(so)
  }

  /**
   * Asserts that the aggregate identifier is equals to the given.
   * @param expected aggregate identifier.
   */
  fun hasAggregateIdentifier(expected: String): IntermediateEventRepresentationAssert {
    isNotNull
    val aggregateIdentifier = actual.aggregateIdentifier.orElse(null)
    if (aggregateIdentifier != expected) {
      failWithMessage("Expected the aggregate identifier to be <%s> but it was <%s>", expected, aggregateIdentifier)
    }
    return this
  }

  /**
   * Asserts that the message identifier is equals to the given.
   * @param expected message identifier.
   */
  fun hasMessageIdentifier(expected: String): IntermediateEventRepresentationAssert {
    isNotNull
    val messageIdentifier = actual.messageIdentifier
    if (messageIdentifier != expected) {
      failWithMessage("Expected the message identifier to be <%s> but it was <%s>", expected, messageIdentifier)
    }
    return this
  }

  /**
   * Asserts that the aggregate type is equals to the given.
   * @param expected aggregate type.
   */
  fun hasAggregateType(expected: String): IntermediateEventRepresentationAssert {
    isNotNull
    val aggregateType = actual.aggregateType.orElseGet(null)
    if (aggregateType != expected) {
      failWithMessage("Expected the aggregate type to be <%s> but it was <%s>", expected, aggregateType)
    }
    return this
  }

  /**
   * Asserts that the content type is equals to the given.
   * @param expected content type.
   */
  fun hasAggregateType(expected: Class<*>): IntermediateEventRepresentationAssert {
    isNotNull
    val contentType = actual.contentType
    if (contentType != expected) {
      failWithMessage("Expected the content type to be <%s> but it was <%s>", expected, contentType)
    }
    return this
  }

  /**
   * Asserts that the type is equals to the given.
   * @param expected type.
   */
  fun hasType(expected: SerializedType): IntermediateEventRepresentationAssert {
    isNotNull
    val type = actual.type
    if (type != expected) {
      failWithMessage("Expected the content type to be <%s> but it was <%s>", expected, type)
    }
    return this
  }

  /**
   * Asserts that the type name is equals to the given.
   * @param expected type.
   */
  fun hasTypeName(expected: String): IntermediateEventRepresentationAssert {
    isNotNull
    val typeName = actual.type.name
    if (typeName != expected) {
      failWithMessage("Expected the content type name to be <%s> but it was <%s>", expected, typeName)
    }
    return this
  }

  /**
   * Asserts that the type name is equals to the given.
   * @param expected type.
   */
  fun hasTypeRevision(expected: String?): IntermediateEventRepresentationAssert {
    isNotNull
    val typeName = actual.type.revision
    if (typeName != expected) {
      failWithMessage("Expected the content type name to be <%s> but it was <%s>", expected, typeName)
    }
    return this
  }

  /**
   * Asserts that the deserialized version of data is equals to deserialized version of given.
   * @param expected intermediate representation.
   * @param T type of the payload.
   */
  fun <T : Any> isEqualDeserializedTo(expected: IntermediateEventRepresentation): IntermediateEventRepresentationAssert {
    isNotNull
    val deserialized: T = serializer.deserialize(actual.data)
    val deserializedExpected: T = serializer.deserialize(expected.data)
    if (deserialized != deserializedExpected) {
      failWithMessage("Expected the content type name to be <%s> but it was <%s>", deserializedExpected, deserialized)
    }
    return this
  }


}
