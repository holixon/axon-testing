package fixture.bankaccount.event;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.axonframework.serialization.Revision;

@Value
@Builder
@Revision("1")
public class MoneyTransferInitializedEvent {

  @NonNull
  String sourceAccountId;

  @NonNull
  String targetAccountId;

  @NonNull
  String transactionId;

  int amount;
}
