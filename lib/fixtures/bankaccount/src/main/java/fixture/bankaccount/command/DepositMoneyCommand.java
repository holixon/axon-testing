package fixture.bankaccount.command;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.serialization.Revision;

@Value
@Revision("1")
@Builder(toBuilder = true)
public class DepositMoneyCommand {

  @NonNull
  @TargetAggregateIdentifier
  String accountId;

  @NonNull Integer amount;
}
