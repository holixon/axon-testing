package io.holixon.axon.testing.upcaster

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.security.AnyTypePermission
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.serialization.xml.XStreamSerializer
import java.util.stream.Stream
import kotlin.streams.toList


class IntermediateRepresentationProviderTest {

  private val xmlSerializer = XStreamSerializer
    .builder()
    .lenientDeserialization()
    .xStream(
      XStream()
        .apply { addPermission(AnyTypePermission.ANY) }
    ).build()


  @UpcasterTest(eventEncoding = UpcasterTest.EventEncoding.JSON)
  fun `receives json files`(events: Stream<String>) {
    assertThat(events.toList()).containsExactly("""{ "json": 1 }""", """{ "json": 2 }""")
  }

  @UpcasterTest(eventEncoding = UpcasterTest.EventEncoding.XML)
  fun `receives xml files`(events: Stream<String>) {
    assertThat(events.toList()).containsExactly("""<node>1</node>""", """<node>2</node>""")
  }

}
