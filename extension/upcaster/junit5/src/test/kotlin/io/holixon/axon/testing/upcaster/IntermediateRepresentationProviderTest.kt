package io.holixon.axon.testing.upcaster

import org.assertj.core.api.Assertions.assertThat
import java.util.stream.Stream
import kotlin.streams.toList


class IntermediateRepresentationProviderTest {

  @UpcasterTest(eventEncoding = UpcasterTest.EventEncoding.JSON)
  fun `receives json files`(events: Stream<String>) {
    assertThat(events.toList()).containsExactly("""{ "json": 1 }""", """{ "json": 2 }""")
  }

  @UpcasterTest(eventEncoding = UpcasterTest.EventEncoding.XML)
  fun `receives xml files`(events: Stream<String>) {
    assertThat(events.toList()).containsExactly("""<node>1</node>""", """<node>2</node>""")
  }

}
