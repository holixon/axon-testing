package fixture.giftcard.query;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "id")
@Builder
public class FindById implements GiftcardQuery {
  @NonNull
  String id;
}
