package fixture.bankaccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import fixture.bankaccount.event.*;
import fixture.bankaccount.query.AccountCurrentBalanceDto;
import fixture.bankaccount.query.AccountFindAllQuery;
import fixture.bankaccount.query.AccountFindByCustomerIdQuery;
import fixture.bankaccount.query.AccountFindByIdQuery;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;

public class AccountCurrentBalanceProjection {

  private final Map<String, AccountCurrentBalanceDto> store = new ConcurrentHashMap<>();

  @EventHandler
  public void on(AccountCreatedEvent evt) {
    store.put(evt.getAccountId(), AccountCurrentBalanceDto.builder()
      .accountId(evt.getAccountId())
      .currentBalance(evt.getInitialBalance())
      .customerId(evt.getCustomerId())
      .build());
  }

  @EventHandler
  public void on(MoneyDepositedEvent evt) {
    update(evt.getAccountId(), evt.getAmount());
  }

  @EventHandler
  public void on(MoneyWithdrawnEvent evt) {
    update(evt.getAccountId(), -evt.getAmount());
  }

  @EventHandler
  void on(MoneyTransferReceivedEvent evt) {
    update(evt.getTargetAccountId(), evt.getAmount());
  }

  @EventHandler
  void on(MoneyTransferCompletedEvent evt) {
    update(evt.getSourceAccountId(), -evt.getAmount());
  }

  private void update(String accountId, int amount) {
    store.computeIfPresent(accountId, (id, dto) -> dto.toBuilder().currentBalance(dto.getCurrentBalance() + amount).build());
  }

  @QueryHandler
  public Optional<AccountCurrentBalanceDto> query(AccountFindByIdQuery query) {
    return Optional.ofNullable(store.get(query.getAccountId()));
  }

  @QueryHandler
  public List<AccountCurrentBalanceDto> query(AccountFindAllQuery query) {
    return new ArrayList<>(store.values());
  }

  @QueryHandler
  public List<AccountCurrentBalanceDto> query(AccountFindByCustomerIdQuery query) {
    return store.values().stream().filter(it -> query.getCustomerId().equals(it.getCustomerId())).collect(Collectors.toList());
  }
}
