package io.holixon.axon.testing.jgiven.example.api;

import lombok.NonNull;
import lombok.Value;

public interface GiftcardQuery {

  class FindAll {

  }

  @Value
  class FindById {
    @NonNull
    String id;
  }
}
