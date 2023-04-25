package io.holixon.axon.testing.upcaster

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.initialEvent
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.jsonTestEventData
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.serialization.Revision
import org.axonframework.serialization.SerializedObject
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.upcasting.event.EventUpcasterChain
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.junit.jupiter.api.Test
import kotlin.streams.toList

/**
 * Let's assume we had a revision "0" of accountCreated which had different field names and one missing required field.
 */
class AccountCreatedV0V1UpcasterTest {
  private val om = jacksonObjectMapper()
  private val jsonSerializer = JacksonSerializer.builder().objectMapper(om).build()

  @Test
  fun `upcast v0 to v1`() {
    // GIVEN: a new event with accountId and required field maximalBalance
    @Revision("1")
    data class AccountCreatedEvent(
      val accountId: String,
      val customerId: String,
      val initialBalance: Int,
      val maximalBalance: Int
    )

    val payloadType = AccountCreatedEvent::class.java.name
    val targetRevision = AccountCreatedEvent::class.java.getAnnotation(Revision::class.java).value

    // AND GIVEN: an old event with revision "0" with bankAccountId (has to be renamed to accountId), and maximalBalance is missing!
    val eventData = jsonTestEventData(
      payloadJson = """{"bankAccountId":"1","customerId":"3","initialBalance":0}""",
      payloadTypeName = payloadType,
      revisionNumber = "0"
    )

    // AND an upcaster we wrote for this usecase
    val upcaster = upcaster(payloadType = payloadType, sourceRevision = eventData.payload.type.revision, targetRevision = targetRevision) {
      (it as ObjectNode).apply {
        put("accountId", get("bankAccountId").asText())
        remove("bankAccountId")
        put("maximalBalance", 1000)
      }
    }

    // WHEN we run the upcaster chain on a stream with a single event
    val serializedResult: SerializedObject<*> = EventUpcasterChain(upcaster)
      .upcast(initialEvent(entry = eventData, serializer = jsonSerializer))
      .toList()
      .first().data

    // AND deserialize to target data class
    val revision1data: AccountCreatedEvent = jsonSerializer.deserialize(serializedResult)

    // THEN the fields are renamed and set
    assertThat(revision1data.accountId).isEqualTo("1")
    assertThat(revision1data.customerId).isEqualTo("3")
    assertThat(revision1data.initialBalance).isEqualTo(0)
    assertThat(revision1data.maximalBalance).isEqualTo(1000)
  }

  private fun upcaster(payloadType: String, sourceRevision: String, targetRevision: String, upcastFunction: (JsonNode) -> JsonNode) =
    object : SingleEventUpcaster() {
      override fun canUpcast(representation: IntermediateEventRepresentation): Boolean =
        representation.type.name == payloadType && representation.type.revision == sourceRevision

      override fun doUpcast(intermediateRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation =
        intermediateRepresentation.upcastPayload(
          SimpleSerializedType(payloadType, targetRevision),
          JsonNode::class.java,
          upcastFunction
        )
    }
}
