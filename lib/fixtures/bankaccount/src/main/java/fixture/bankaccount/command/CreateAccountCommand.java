package fixture.bankaccount.command;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.serialization.Revision;

@Value
@Builder(toBuilder = true)
@Revision("1")
public class CreateAccountCommand {

  @NonNull
  @TargetAggregateIdentifier
  String accountId;

  @NonNull
  String customerId;

  Integer initialBalance;

  Integer maximalBalance;

}
