package fixture.giftcard;

import fixture.giftcard.command.IssueCommand;
import fixture.giftcard.command.RedeemCommand;
import fixture.giftcard.event.IssuedEvent;
import fixture.giftcard.event.RedeemedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;

import static com.google.common.base.Preconditions.checkArgument;

@NoArgsConstructor
@ToString
public class GiftcardAggregate {

  @CommandHandler
  public static GiftcardAggregate create(IssueCommand cmd) {
    checkArgument(cmd.getInitialBalance() > 0, "initial balance has to be > 0, %s", cmd);

    AggregateLifecycle.apply(IssuedEvent.builder()
      .id(cmd.getId())
      .initialBalance(cmd.getInitialBalance())
      .build());
    return new GiftcardAggregate();
  }

  @AggregateIdentifier
  @Getter
  private String id;

  @Getter
  private int balance;

  @EventSourcingHandler
  public void on(IssuedEvent evt) {
    this.id = evt.getId();
    this.balance = evt.getInitialBalance();
  }

  @CommandHandler
  public void handle(RedeemCommand cmd) {
    checkArgument(cmd.getAmount() > 0, "amount has to be > 0: %s", cmd);
    checkArgument(balance >= cmd.getAmount(), "card %s has insufficient balance: %s: %s", id, balance, cmd);

    AggregateLifecycle.apply(RedeemedEvent.builder()
      .id(cmd.getId())
      .amount(cmd.getAmount())
      .build());
  }

  @EventSourcingHandler
  public void on(RedeemedEvent evt) {
    this.balance -= evt.getAmount();
  }
}
