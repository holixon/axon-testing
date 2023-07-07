package io.holixon.axon.testing.upcaster

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.security.AnyTypePermission
import io.holixon.axon.testing.upcaster.content.DefaultStringMessageContentProvider
import io.holixon.axon.testing.upcaster.payloadtype.FilenameBasedPayloadTypeAndRevisionProvider
import io.holixon.axon.testing.upcaster.payloadtype.FixedPayloadTypeAndRevisionProvider
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.xml.XStreamSerializer


class IntermediateRepresentationProviderTest {

  companion object {
    val xmlSerializer = XStreamSerializer
      .builder()
      .lenientDeserialization()
      .xStream(
        XStream()
          .apply { addPermission(AnyTypePermission.ANY) }
      ).build()

    val jsonValueSerializer = JacksonSerializer.builder().objectMapper(jacksonObjectMapper().findAndRegisterModules()).build()

    val messageContentProvider: MessageContentProvider = DefaultStringMessageContentProvider()
    val payloadTypeAndRevisionProvider: PayloadTypeAndRevisionProvider = FixedPayloadTypeAndRevisionProvider("event", "1")
  }

  @UpcasterTest(
    messageEncoding = MessageEncoding.JACKSON,
  )
  fun `receives json files`(events: List<String>) {
    assertThat(events).containsExactly("""{ "json": 1 }""", """{ "json": 2 }""")
  }

  @UpcasterTest(
    messageEncoding = MessageEncoding.JACKSON,
  )
  fun `receives json events`(events: List<IntermediateEventRepresentation>) {
    assertThat(events).hasSize(2)
  }

  @UpcasterTest(
    messageEncoding = MessageEncoding.XSTREAM,
    payloadTypeProvider = FilenameBasedPayloadTypeAndRevisionProvider::class,
    messageContentProvider = DefaultStringMessageContentProvider::class
  )
  fun `receives xml files`(events: List<String>) {
    assertThat(events).containsExactly("""<node>1</node>""", """<node>2</node>""")
  }

}
