package fixture.giftcard.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public interface GiftcardCommand {

  @TargetAggregateIdentifier
  String getId();

}
