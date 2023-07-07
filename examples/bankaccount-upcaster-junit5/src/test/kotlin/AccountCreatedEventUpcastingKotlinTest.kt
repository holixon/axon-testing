package io.holixon.axon.testing.examples.upcaster.junit5.kotlin

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.security.AnyTypePermission
import fixture.bankaccount.event.AccountCreatedEvent
import io.holixon.axon.testing.upcaster.MessageEncoding
import io.holixon.axon.testing.upcaster.UpcasterTest
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.deserializeEvents
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.jsonNodeUpcaster
import io.holixon.axon.testing.upcaster.UpcasterTestSupport.Companion.xmlDocumentUpcaster
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.xml.XStreamSerializer
import org.dom4j.Document
import org.dom4j.Element
import java.util.stream.Collectors
import java.util.stream.Stream

class AccountCreatedEventUpcastingKotlinTest {

  companion object {
    private val xmlSerializer: XStreamSerializer =
      XStreamSerializer.builder().lenientDeserialization().xStream(XStream().apply { addPermission(AnyTypePermission.ANY) }).build()
    private val jacksonSerializer: JacksonSerializer = JacksonSerializer.builder().lenientDeserialization().objectMapper(jacksonObjectMapper()).build()

    private val payloadType: String = AccountCreatedEvent::class.java.name
    private val xmlUpcaster = xmlDocumentUpcaster(payloadType, "0", "1") {
      it.apply {
        selectNodes("//$payloadType").forEach { node ->
          // replace bank account id with account id
          val accountId = (node as Element).addElement("accountId")
          val bankAccountId = node.element("bankAccountId")
          accountId.addText(bankAccountId.text)
          node.remove(bankAccountId)

          // add max balance
          val maxBalance: Element = node.addElement("maximalBalance")
          maxBalance.addText("1000")
        }
      }
    }

    private val jsonUpcaster = jsonNodeUpcaster(payloadType, "12", "13") {
      (it as ObjectNode).apply {
        put("accountId", get("bankAccountId").asText())
        remove("bankAccountId")
        put("maximalBalance", 1000)
      }
    }

    private val accountEvent = AccountCreatedEvent
      .builder()
      .accountId("4711")
      .customerId("Customer1")
      .initialBalance(100)
      .maximalBalance(1000)
      .build()
  }


  @UpcasterTest(
    messageEncoding = MessageEncoding.XSTREAM
  )
  fun upcasts_account_created_xstream(events: List<IntermediateEventRepresentation>) {

    val upcastedStream = xmlUpcaster.upcast(events.stream())
    val upcastedEvents = deserializeEvents<AccountCreatedEvent>(upcastedStream, xmlSerializer)

    // FIXME: build better assertions

    assertThat(upcastedEvents)
      .hasSize(1)
      .element(0)
      .isEqualTo(accountEvent)
  }

  @UpcasterTest(
    messageEncoding = MessageEncoding.JACKSON
  )
  fun `upcasts account created jackson`(events: List<IntermediateEventRepresentation>, result: List<IntermediateEventRepresentation>) {

    val upcastedStream = jsonUpcaster.upcast(events.stream())

    // FIXME: build better assertions
    val upcastedEvents = deserializeEvents<AccountCreatedEvent>(stream = upcastedStream, serializer = jacksonSerializer).collect(Collectors.toList())
    val deserializedResult = deserializeEvents<AccountCreatedEvent>(list = result, serializer = jacksonSerializer)

    assertThat(upcastedEvents).containsExactlyElementsOf(deserializedResult)
  }
}
