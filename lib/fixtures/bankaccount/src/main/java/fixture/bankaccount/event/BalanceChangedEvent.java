package fixture.bankaccount.event;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class BalanceChangedEvent {

  @NonNull
  String accountId;

  int newBalance;

}
