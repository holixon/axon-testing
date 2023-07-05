package io.holixon.axon.testing.upcaster

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.security.AnyTypePermission
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.initialEvent
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.xmlDocumentUpcaster
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.xmlTestEventData
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.serialization.upcasting.event.EventUpcasterChain
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.xml.XStreamSerializer
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.streams.toList

class DummyUpcasterParametrizedTest {

  companion object {
    private val xmlSerializer = XStreamSerializer
      .builder()
      .lenientDeserialization()
      .xStream(
        XStream()
          .apply { addPermission(AnyTypePermission.ANY) }
      ).build()
    private val fixedPayloadTypeAndRevisionProvider = FixedPayloadTypeAndRevisionProvider(DummyEvent::class.java.name, "1")

    @JvmStatic
    fun createStream(): Stream<Stream<IntermediateEventRepresentation>> {

      val xml = """
      <io.holixon.axon.testing.upcaster.DummyEvent><someValue>some</someValue></io.holixon.axon.testing.upcaster.DummyEvent>
      """.trimIndent()

      return Stream.of(
        initialEvent(
          entry = xmlTestEventData(
            xml,
            fixedPayloadTypeAndRevisionProvider.getPayloadType(xml),
            fixedPayloadTypeAndRevisionProvider.getRevision(xml)
          ),
          serializer = xmlSerializer
        )
      )
    }
  }

  @ParameterizedTest
  @MethodSource("io.holixon.axon.testing.upcaster.DummyUpcasterParametrizedTest#createStream")
  fun `should upcast xml just changing the target revision`(stream: Stream<IntermediateEventRepresentation>) {

    val xmlUpcasters = EventUpcasterChain(
      xmlDocumentUpcaster(DummyEvent::class, "1", "2") {
        it // do nothing
      }
    )

    val result = xmlUpcasters.upcast(
      stream
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

