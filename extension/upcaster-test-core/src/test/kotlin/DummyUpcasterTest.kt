package io.holixon.axon.testing.upcaster

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.initialEvent
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.jsonTestEventData
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.xmlTestEventData
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.serialization.Revision
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.upcasting.event.EventUpcasterChain
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.axonframework.serialization.xml.XStreamSerializer
import org.dom4j.Document
import org.junit.Test
import java.util.function.Function
import kotlin.streams.toList

class DummyUpcasterTest {

  private val jsonSerializer = JacksonSerializer.builder().objectMapper(jacksonObjectMapper()).build()
  private val jsonUpcasters = EventUpcasterChain(DummyEventJsonUpcaster())

  private val xmlSerializer = XStreamSerializer.builder().build()
  private val xmlUpcasters = EventUpcasterChain(DummyEventXmlUpcaster())

  @Test
  fun `should upcast json`() {

    val json = """
      {"value":"some"}
      """.trimIndent()

    val result = jsonUpcasters.upcast(initialEvent(jsonTestEventData(json, "io.holixon.axon.testing.upcaster.DummyEvent", "{}", "1"), jsonSerializer)).toList()

    assertThat(result)
      .isNotNull
      .hasSize(1)
      .element(0)
      .extracting { it.data }
      .isNotNull

    val event: DummyEvent = jsonSerializer.deserialize(result[0].data)
    assertThat(event).isNotNull
  }

  @Test
  fun `should upcast xml`() {

    val xml = """
      <io.holixon.axon.testing.upcaster.DummyEvent><value>some</value></io.holixon.axon.testing.upcaster.DummyEvent>
      """.trimIndent()

    val result = xmlUpcasters.upcast(initialEvent(xmlTestEventData(xml, "io.holixon.axon.testing.upcaster.DummyEvent", "1"), xmlSerializer)).toList()

    assertThat(result)
      .isNotNull
      .hasSize(1)
      .element(0)
      .extracting { it.data }
      .isNotNull

    val event: DummyEvent = xmlSerializer.deserialize(result[0].data)
    assertThat(event).isNotNull
  }

}

@Revision("2")
data class DummyEvent(
  val value: String
)


class DummyEventJsonUpcaster : SingleEventUpcaster() {

  override fun canUpcast(representation: IntermediateEventRepresentation): Boolean =
    representation.type.name == DummyEvent::class.java.name && representation.type.revision == "1"


  override fun doUpcast(representation: IntermediateEventRepresentation): IntermediateEventRepresentation {
    return representation.upcastPayload(
      SimpleSerializedType(DummyEvent::class.java.name, "2"),
      String::class.java,
      Function.identity()
    )
  }
}

class DummyEventXmlUpcaster : SingleEventUpcaster() {

  override fun canUpcast(representation: IntermediateEventRepresentation): Boolean =
    representation.type.name == DummyEvent::class.java.name && representation.type.revision == "1"

  override fun doUpcast(representation: IntermediateEventRepresentation): IntermediateEventRepresentation {
    return representation.upcastPayload(
      SimpleSerializedType(DummyEvent::class.java.name, "2"),
      Document::class.java,
      Function.identity()
    )
  }
}
