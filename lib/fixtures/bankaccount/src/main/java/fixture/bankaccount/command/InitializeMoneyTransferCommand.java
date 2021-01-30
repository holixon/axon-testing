package fixture.bankaccount.command;

import java.util.UUID;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.serialization.Revision;

@Value
@Builder
@Revision("1")
public class InitializeMoneyTransferCommand {

  @NonNull
  @TargetAggregateIdentifier
  String sourceAccountId;

  @NonNull
  String targetAccountId;

  @Builder.Default
  String transactionId = UUID.randomUUID().toString();

  int amount;

}
