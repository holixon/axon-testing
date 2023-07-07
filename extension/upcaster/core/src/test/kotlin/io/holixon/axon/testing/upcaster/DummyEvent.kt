package io.holixon.axon.testing.upcaster

import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.serialization.Revision

@Revision(value = "2")
data class DummyEvent(
  @JsonProperty("someValue")
  val someValue: String
)
