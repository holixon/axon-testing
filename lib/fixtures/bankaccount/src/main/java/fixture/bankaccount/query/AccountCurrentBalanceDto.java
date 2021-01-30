package fixture.bankaccount.query;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class AccountCurrentBalanceDto {

  String accountId;

  String customerId;

  int currentBalance;

}
