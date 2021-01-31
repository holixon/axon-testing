package fixture.giftcard.event;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class IssuedEvent implements GiftcardEvent {

  @NonNull
  String id;

  int initialBalance;

}
