package io.holixon.axon.testing.jgiven.example.api;

import lombok.NonNull;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public interface GiftcardCommand {

  @TargetAggregateIdentifier
  String getId();

  @Value
  class RedeemCommand implements GiftcardCommand {

    @NonNull
    String id;

    int amount;
  }


  @Value
  class IssueCommand implements GiftcardCommand {

    @NonNull
    String id;

    int initialBalance;
  }


}
