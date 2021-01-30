package io.holixon.axon.testing.example.junit.java;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import io.holixon.axon.testing.jgiven.example.GiftcardAggregate;
import io.holixon.axon.testing.jgiven.example.api.GiftcardCommand.IssueCommand;
import io.holixon.axon.testing.jgiven.example.api.GiftcardCommand.RedeemCommand;
import io.holixon.axon.testing.jgiven.example.api.GiftcardEvent.IssuedEvent;
import io.holixon.axon.testing.jgiven.example.api.GiftcardEvent.RedeemedEvent;
import io.holixon.axon.testing.jgiven.junit.AggregateFixtureScenarioTest;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Test;

public class GiftcardAggregateTest extends AggregateFixtureScenarioTest<GiftcardAggregate> {

  @ProvidedScenarioState
  private final AggregateTestFixture<GiftcardAggregate> fixture = new AggregateTestFixture<>(GiftcardAggregate.class);

  @Test
  public void issue_a_giftcard() {
    given()
      .noPriorActivity()
    ;

    when()
      .command(new IssueCommand("1", 100))
    ;

    then()
      .expectEvent(new IssuedEvent("1", 100))
    ;
  }

  @Test
  public void redeem_50() {
    given()
      .event(new IssuedEvent("1", 100));

    when()
      .command(new RedeemCommand("1", 50));

    then()
      .expectEvent(new RedeemedEvent("1", 50));
  }

  @Test
  public void redeem_twice_by_commands() {
    given()
      .command(new IssueCommand("1", 100))
      .and()
      .command(new RedeemCommand("1", 40))
    ;

    when()
      .command(new RedeemCommand("1", 40))
    ;

    then()
      .expectEvent(new RedeemedEvent("1", 40))
      .and()
      .expectState("balance", 20, GiftcardAggregate::getBalance)
    ;
  }

  @Test
  public void redeem_by_vararg_commands() {
    given()
      .command(new IssueCommand("1", 100))
      .and()
      .commands(new RedeemCommand("1", 40), new RedeemCommand("1", 30))
    ;

    when()
      .command(new RedeemCommand("1", 20))
    ;

    then()
      .expectState("balance", 10, GiftcardAggregate::getBalance)
    ;
  }
}
