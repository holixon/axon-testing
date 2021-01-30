package fixture.bankaccount.event;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class MoneyTransferRolledBackEvent {

  @NonNull
  String sourceAccountId;

  @NonNull
  String transactionId;

}
