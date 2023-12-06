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
import org.dom4j.io.DOMWriter
import org.json.JSONObject
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.xmlunit.assertj3.XmlAssert
import org.xmlunit.builder.Input


/**
 * Asserts intermediate representation.
 */
class IntermediateEventRepresentationAssert(
  actual: IntermediateEventRepresentation,
  private val serializer: Serializer,
  /**
   * Parameter controlling if the strict serialized data check is executed. Defaults to false.
   */
  private val strictSerializedComparison: Boolean = false,
  private val intermediateRepresentationTypeResolver: (serializer: Serializer) -> Class<*> =
    {
      when (it) {
        is XStreamSerializer -> Document::class.java
        is JacksonSerializer -> JsonNode::class.java
        else -> throw IllegalArgumentException("Unknown serializer type ${serializer::class.java}")
      }
    },
  private val intermediateRepresentationObjectAssertResolver: (serializer: Serializer, strict: Boolean) -> (expected: Any, actual: Any) -> Unit =
    { it: Serializer, strict: Boolean ->
      when (it) {
        is XStreamSerializer -> { expected: Any, actual: Any ->
          require(expected is Document) { "The expected intermediate representation must be a Document, but it was of type ${expected::class.java.name}" }
          require(actual is Document) { "The actual intermediate representation must be a Document, but it was of type ${expected::class.java.name}" }
          XmlAssert
            .assertThat(
              Input.fromDocument(
                DOMWriter().write(actual)
              )
            ).and(
              Input.fromDocument(
                DOMWriter().write(expected)
              )
            )
            .apply {
              if (strict) {
                this.areIdentical()
              } else {
                this
                  .ignoreWhitespace() // ignore empty lines and other empty text nodes
                  .ignoreChildNodesOrder() // child order doesn't matter
                  .areSimilar() // just be similar
              }
            }
        }

        is JacksonSerializer -> { expected: Any, actual: Any ->
          require(expected is JSONObject) { "The expected intermediate representation must be a JSONObject, but it was of type ${expected::class.java.name}" }
          require(actual is JSONObject) { "The actual intermediate representation must be a JSONObject, but it was of type ${expected::class.java.name}" }
          JSONAssert.assertEquals(
            expected,
            actual,
            if (strict) {
              JSONCompareMode.STRICT
            } else {
              JSONCompareMode.LENIENT
            }
          )
        }

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
    fun assertThat(actual: IntermediateEventRepresentation, serializer: Serializer) = assertThat(actual, serializer, false)

    /**
     * Creates the assert for given intermediate representation serialized using given serializer.
     * @param actual current intermediate representation.
     * @param serializer serializer in use.
     * @param strictSerializedComparison controlling if the strict serialized data check is executed.
     * @return the asserting anchor for checks.
     */
    @JvmStatic
    fun assertThat(actual: IntermediateEventRepresentation, serializer: Serializer, strictSerializedComparison: Boolean) =
      IntermediateEventRepresentationAssert(actual, serializer, strictSerializedComparison)
  }

  /**
   * Asserts that the deserialized version of data is equals to deserialized version of given.
   * We are comparing on the level of deserialized target type.
   *
   * @param expected intermediate representation.
   * @param T type of the payload.
   */
  fun <T : Any> isEqualDeserializedTo(expected: IntermediateEventRepresentation): IntermediateEventRepresentationAssert {
    isNotNull
    val intermediateRepresentationType = intermediateRepresentationTypeResolver.invoke(serializer)
    val deserialized: T = serializer.deserialize(actual.getData(intermediateRepresentationType))
    val deserializedExpected: T = serializer.deserialize(expected.getData(intermediateRepresentationType))
    if (deserialized != deserializedExpected) {
      failWithMessage("Expected the deserialized content to be <%s> but it was <%s>", deserializedExpected, deserialized)
    }
    return this
  }

  /**
   * Asserts equality of type and data in the intermediate form (will compare Document, JsonNode, ...).
   * @param expected intermediate or initial representation of expected event.
   * @return object assert.
   */
  override fun isEqualTo(expected: Any): IntermediateEventRepresentationAssert {
    isNotNull
    when (expected) {
      is IntermediateEventRepresentation -> {
        hasType(expected.type)
        val intermediateRepresentationType = intermediateRepresentationTypeResolver.invoke(serializer)
        val expectedData = expected.getData(intermediateRepresentationType)
        val actualData = actual.getData(intermediateRepresentationType)
        val objectAssertFunction = intermediateRepresentationObjectAssertResolver.invoke(serializer, strictSerializedComparison)
        objectAssertFunction.invoke(expectedData.data, actualData.data) // comparing intermediate representation data (Document, JsonNode, ...)
      }

      else -> failWithMessage(
        "Expected to compare current expected representation with an instance of IntermediateEventRepresentation, but it was an instance of <%s>",
        expected.javaClass
      )
    }
    return this
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
    val data = actual.getData(intermediateRepresentationType) // type is the type of intermediate data
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
      failWithMessage("Expected the type to be <%s> but it was <%s>", expected, type)
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
      failWithMessage("Expected the content type revision to be <%s> but it was <%s>", expected, typeName)
    }
    return this
  }


}
