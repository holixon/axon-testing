package io.holixon.axon.test.jgiven.example.api;

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
