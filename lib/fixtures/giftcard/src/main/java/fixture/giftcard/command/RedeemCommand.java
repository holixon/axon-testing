package fixture.giftcard.command;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class RedeemCommand implements GiftcardCommand {

  @NonNull
  String id;

  int amount;
}
