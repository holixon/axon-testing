package io.holixon.axon.testing.jgiven.example.api;

import lombok.NonNull;
import lombok.Value;

public interface GiftcardEvent {

  String getId();

  @Value
  class IssuedEvent implements GiftcardEvent {

    @NonNull
    String id;

    int initialBalance;

  }

  @Value
  class RedeemedEvent implements GiftcardEvent{

    @NonNull
    String id;

    int amount;

  }

}
