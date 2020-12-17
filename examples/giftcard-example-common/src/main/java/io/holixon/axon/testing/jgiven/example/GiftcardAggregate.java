package io.holixon.axon.testing.jgiven.example;

import io.holixon.axon.testing.jgiven.example.api.GiftcardCommand;
import io.holixon.axon.testing.jgiven.example.api.GiftcardEvent;
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
  public static GiftcardAggregate create(GiftcardCommand.IssueCommand cmd) {
    checkArgument(cmd.getInitialBalance() > 0, "initial balance has to be > 0, %s", cmd);

    AggregateLifecycle.apply(new GiftcardEvent.IssuedEvent(cmd.getId(), cmd.getInitialBalance()));
    return new GiftcardAggregate();
  }

  @AggregateIdentifier
  private String id;

  private int balance;

  @EventSourcingHandler
  public void on(GiftcardEvent.IssuedEvent evt) {
    this.id = evt.getId();
    this.balance = evt.getInitialBalance();
  }

  @CommandHandler
  public void handle(GiftcardCommand.RedeemCommand cmd) {
    checkArgument(cmd.getAmount() > 0, "amount has to be > 0: %s", cmd);
    checkArgument(balance >= cmd.getAmount(), "card %s has insufficient balance: %s: %s", id, balance, cmd);

    AggregateLifecycle.apply(new GiftcardEvent.RedeemedEvent(cmd.getId(), cmd.getAmount()));
  }

  @EventSourcingHandler
  public void on(GiftcardEvent.RedeemedEvent evt) {
    this.balance -= evt.getAmount();
  }
}
