package io.holixon.axon.testing.jgiven.example.api;

import lombok.NonNull;
import lombok.Value;

public interface GiftcardQuery {

  class FindAll implements GiftcardQuery {
    public static final FindAll INSTANCE = new FindAll();
  }

  @Value
  class FindById implements GiftcardQuery {
    @NonNull
    String id;
  }
}
