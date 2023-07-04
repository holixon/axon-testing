package io.holixon.axon.testing.upcaster

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.security.AnyTypePermission
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.EMPTY_JSON
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.initialEvent
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.jsonNodeUpcaster
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.jsonTestEventData
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.xmlDocumentUpcaster
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.xmlTestEventData
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.serialization.Revision
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.upcasting.event.EventUpcasterChain
import org.axonframework.serialization.xml.XStreamSerializer
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.streams.toList

class DummyUpcasterTest {

  @Test
  @Disabled // FIXME -> understand this!
  fun `should upcast json just changing the target revision`() {

    val jsonSerializer = JacksonSerializer.builder().objectMapper(jacksonObjectMapper()).defaultTyping().lenientDeserialization().build()

    val jsonUpcasters = EventUpcasterChain(
      jsonNodeUpcaster(DummyEvent::class, "1", "2") {
        it // do nothing
      }
    )

    val json = """
      {"someValue":"some"}
      """.trimIndent()

    val result = jsonUpcasters.upcast(
      initialEvent(jsonTestEventData(json, DummyEvent::class, EMPTY_JSON, "1"), jsonSerializer)
    ).toList()

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
  fun `should upcast xml just changing the target revision`() {

    val xmlSerializer = XStreamSerializer
      .builder()
      .lenientDeserialization()
      .xStream(
        XStream()
          .apply { addPermission(AnyTypePermission.ANY) }
      ).build()

    val xmlUpcasters = EventUpcasterChain(
      xmlDocumentUpcaster(DummyEvent::class, "1", "2") {
        it // do nothing
      }
    )

    val xml = """
      <io.holixon.axon.testing.upcaster.DummyEvent><someValue>some</someValue></io.holixon.axon.testing.upcaster.DummyEvent>
      """.trimIndent()

    val result = xmlUpcasters.upcast(
      initialEvent(
        xmlTestEventData(
          xml,
          DummyEvent::class.java.name, "1"
        ), xmlSerializer
      )
    ).toList()

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

@Revision(value = "2")
data class DummyEvent(
  @JsonProperty("someValue")
  val someValue: String
)
