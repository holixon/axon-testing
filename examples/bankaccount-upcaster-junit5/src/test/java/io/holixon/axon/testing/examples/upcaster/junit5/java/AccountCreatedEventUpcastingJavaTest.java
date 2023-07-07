package io.holixon.axon.testing.examples.upcaster.junit5.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import fixture.bankaccount.event.AccountCreatedEvent;
import io.holixon.axon.testing.upcaster.MessageEncoding;
import io.holixon.axon.testing.upcaster.UpcasterTest;
import lombok.val;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.List;
import java.util.stream.Collectors;

import static io.holixon.axon.testing.upcaster.UpcasterTestSupport.*;
import static org.assertj.core.api.Assertions.*;

public class AccountCreatedEventUpcastingJavaTest {
  private final static XStreamSerializer xmlSerializer = createXstreamSerializer();
  private final static JacksonSerializer jacksonSerializer = createJacksonSerializer();
  private final static String payloadType = AccountCreatedEvent.class.getName();
  private final static SingleEventUpcaster xmlDocumentUpcaster = xmlDocumentUpcaster(payloadType, "0", "1", (document) -> {
    document.selectNodes("//" + payloadType).forEach(node -> {
      // replace bank account id with account id
      Element accountId = ((Element) node).addElement("accountId");
      Element bankAccountId = ((Element) node).element("bankAccountId");
      accountId.addText(bankAccountId.getText());
      ((Element) node).remove(bankAccountId);

      // add max balance
      Element maxBalance = ((Element) node).addElement("maximalBalance");
      maxBalance.addText("1000");
    });
    return document;
  });

  private final static SingleEventUpcaster jsonNodeUpcaster = jsonNodeUpcaster(payloadType, "12", "13", (node) -> {
    val root = (ObjectNode)node;
    root.set("accountId", root.get("bankAccountId"));
    root.remove("bankAccountId");
    root.put("maximalBalance", 1000);
    return root;
  });



  @UpcasterTest(
    messageEncoding = MessageEncoding.XSTREAM
  )
  public void upcasts_account_created_xstream(List<IntermediateEventRepresentation> events) {


    val upcastedStream = xmlDocumentUpcaster.upcast(events.stream());

    // FIXME: build better assertions
    val upcastedEvents = upcastedStream.map((ier) -> xmlSerializer.deserialize(ier.getData(Document.class))).collect(Collectors.toList());
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
      );
  }

  @UpcasterTest(
    messageEncoding = MessageEncoding.JACKSON
  )
  public void upcasts_account_created_jackson(List<IntermediateEventRepresentation> events) {

    val upcastedStream = jsonNodeUpcaster.upcast(events.stream());

    // FIXME: build better assertions
    val upcastedEvents = upcastedStream.map((ier) -> jacksonSerializer.deserialize(ier.getData())).collect(Collectors.toList());

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
      );
  }

  static XStreamSerializer createXstreamSerializer() {
    val xstream = new XStream();
    xstream.addPermission(AnyTypePermission.ANY);
    return XStreamSerializer.builder().lenientDeserialization().xStream(xstream).build();
  }

  static JacksonSerializer createJacksonSerializer() {
    val objectMapper = new ObjectMapper().registerModule(new KotlinModule.Builder().build());
    return JacksonSerializer.builder().lenientDeserialization().objectMapper(objectMapper).build();
  }
}
