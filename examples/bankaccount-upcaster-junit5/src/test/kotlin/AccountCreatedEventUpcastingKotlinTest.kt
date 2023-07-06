package io.holixon.axon.testing.examples.upcaster.junit5.kotlin

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.security.AnyTypePermission
import fixture.bankaccount.event.AccountCreatedEvent
import io.holixon.axon.testing.upcaster.MessageEncoding
import io.holixon.axon.testing.upcaster.UpcasterTest
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.jsonNodeUpcaster
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.xmlDocumentUpcaster
import io.holixon.axon.testing.upcaster.content.DefaultStringMessageContentProvider
import io.holixon.axon.testing.upcaster.payloadtype.FilenameBasedPayloadTypeAndRevisionProvider
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.axonframework.serialization.xml.XStreamSerializer
import java.util.stream.Stream

class AccountCreatedEventUpcastingKotlinTest {

  companion object {
    private val xmlSerializer: XStreamSerializer =
      XStreamSerializer.builder().lenientDeserialization().xStream(XStream().apply { addPermission(AnyTypePermission.ANY) }).build()
    private val jacksonSerializer: JacksonSerializer = JacksonSerializer.builder().lenientDeserialization().objectMapper(jacksonObjectMapper()).build()
  }

  @UpcasterTest(
    messageEncoding = MessageEncoding.XSTREAM,
    payloadTypeProvider = FilenameBasedPayloadTypeAndRevisionProvider::class,
    messageContentProvider = DefaultStringMessageContentProvider::class
  )
  fun upcasts_account_created_xstream(events: List<IntermediateEventRepresentation?>) {
    val payloadType: String = AccountCreatedEvent::class.java.name
    val upcaster: SingleEventUpcaster = xmlDocumentUpcaster(payloadType, "0", "1") {
      it.apply {
        selectNodes("//$payloadType").forEach { node ->
          // replace bank account id with account id
          val accountId = (node as org.dom4j.Element).addElement("accountId")
          val bankAccountId = node.element("bankAccountId")
          accountId.addText(bankAccountId.text)
          node.remove(bankAccountId)

          // add max balance
          val maxBalance: org.dom4j.Element = node.addElement("maximalBalance")
          maxBalance.addText("1000")
        }
      }
    }
    val upcastedStream: Stream<IntermediateEventRepresentation> = upcaster.upcast(events.stream())
    val upcastedEvents = upcastedStream.map { ier ->
      xmlSerializer.deserialize<org.dom4j.Document, Any>(
        ier.getData(
          org.dom4j.Document::class.java
        )
      )
    }
    assertThat(upcastedEvents)
      .hasSize(1)
      .element(0)
      .isEqualTo(
        AccountCreatedEvent
          .builder()
          .accountId("4711")
          .customerId("Customer1")
          .initialBalance(100)
          .maximalBalance(1000)
          .build()
      )
  }

  @UpcasterTest(
    messageEncoding = MessageEncoding.JACKSON,
    payloadTypeProvider = FilenameBasedPayloadTypeAndRevisionProvider::class,
    messageContentProvider = DefaultStringMessageContentProvider::class
  )
  fun upcasts_account_created_jackson(events: List<IntermediateEventRepresentation?>) {
    val payloadType: String = AccountCreatedEvent::class.java.name
    val upcaster: SingleEventUpcaster = jsonNodeUpcaster(payloadType, "12", "13") {
      (it as ObjectNode).apply {
        put("accountId", get("bankAccountId").asText())
        remove("bankAccountId")
        put("maximalBalance", 1000)
      }
    }
    val upcastedStream: Stream<IntermediateEventRepresentation> = upcaster.upcast(events.stream())
    val upcastedEvents = upcastedStream.map { ier ->
      val event: AccountCreatedEvent = jacksonSerializer.deserialize(
        ier.data
      )
      event
    }
    assertThat(upcastedEvents)
      .hasSize(1)
      .element(0)
      .isEqualTo(
        AccountCreatedEvent
          .builder()
          .accountId("4711")
          .customerId("Customer1")
          .initialBalance(100)
          .maximalBalance(1000)
          .build()
      )
  }
}
