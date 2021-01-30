package fixture.bankaccount;

import fixture.bankaccount.command.CompleteMoneyTransferCommand;
import fixture.bankaccount.command.ReceiveMoneyTransferCommand;
import fixture.bankaccount.command.RollBackMoneyTransferCommand;
import fixture.bankaccount.event.MoneyTransferCompletedEvent;
import fixture.bankaccount.event.MoneyTransferInitializedEvent;
import fixture.bankaccount.event.MoneyTransferReceivedEvent;
import fixture.bankaccount.event.MoneyTransferRolledBackEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;

@Saga
@Slf4j
public class MoneyTransferSaga {
  public static final String TRANSACTION_ID = "transactionId";

  @Setter
  private transient CommandGateway commandGateway;

  @Getter
  private String sourceAccountId;

  @Getter
  private String targetAccountId;

  @Getter
  private int amount;

  @StartSaga
  @SagaEventHandler(associationProperty = TRANSACTION_ID)
  void on(MoneyTransferInitializedEvent evt) {
    log.info("Saga started: {}", evt);

    sourceAccountId = evt.getSourceAccountId();
    targetAccountId = evt.getTargetAccountId();
    amount = evt.getAmount();

    try {
      commandGateway.sendAndWait(ReceiveMoneyTransferCommand.builder()
        .amount(evt.getAmount())
        .sourceAccountId(sourceAccountId)
        .targetAccountId(targetAccountId)
        .transactionId(evt.getTransactionId())
        .build()
    );
    } catch (CommandExecutionException e) {
      commandGateway.sendAndWait(RollBackMoneyTransferCommand.builder()
        .sourceAccountId(sourceAccountId)
        .transactionId(evt.getTransactionId())
        .build());
    }
  }

  @SagaEventHandler(associationProperty = TRANSACTION_ID)
  void on(MoneyTransferReceivedEvent evt) {
    log.info("Saga: {}", evt);

    commandGateway.sendAndWait(CompleteMoneyTransferCommand.builder()
      .amount(amount)
      .sourceAccountId(sourceAccountId)
      .transactionId(evt.getTransactionId())
      .build()
    );
  }

  @EndSaga
  @SagaEventHandler(associationProperty = TRANSACTION_ID)
  void on(MoneyTransferCompletedEvent evt) {
    log.info("Saga: {}", evt);
  }

  @EndSaga
  @SagaEventHandler(associationProperty = TRANSACTION_ID)
  void on(MoneyTransferRolledBackEvent evt) {
    log.error("Saga: {}", evt);
  }
}
