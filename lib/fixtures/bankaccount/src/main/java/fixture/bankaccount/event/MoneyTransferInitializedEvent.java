package fixture.bankaccount.event;

import lombok.Builder;
import lombok.Value;
import org.axonframework.serialization.Revision;

@Value
@Builder
@Revision("1")
public class MoneyTransferInitializedEvent {

  String sourceAccountId;
  String targetAccountId;

  String transactionId;

  int amount;
}
