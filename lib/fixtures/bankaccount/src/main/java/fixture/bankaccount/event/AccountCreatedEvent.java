package fixture.bankaccount.event;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.axonframework.serialization.Revision;

@Value
@Builder(toBuilder = true)
@Revision("1")
public class AccountCreatedEvent {

  @NonNull
  String accountId;

  @NonNull
  String customerId;

  int initialBalance;

  int maximalBalance;

}
