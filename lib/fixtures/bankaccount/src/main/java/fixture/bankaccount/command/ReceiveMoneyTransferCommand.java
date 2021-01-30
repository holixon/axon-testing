package fixture.bankaccount.command;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
@Builder
public class ReceiveMoneyTransferCommand {

  @TargetAggregateIdentifier
  @NonNull
  String targetAccountId;

  @NonNull
  String sourceAccountId;

  @NonNull
  String transactionId;

  int amount;

}
