package fixture.giftcard.command;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class IssueCommand implements GiftcardCommand {

  @NonNull
  String id;

  int initialBalance;
}
