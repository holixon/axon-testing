package fixture.giftcard.event;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class RedeemedEvent implements GiftcardEvent {

  @NonNull
  String id;

  int amount;

}
