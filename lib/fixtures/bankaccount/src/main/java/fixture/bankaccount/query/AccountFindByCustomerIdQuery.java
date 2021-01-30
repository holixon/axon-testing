package fixture.bankaccount.query;

import lombok.Builder;
import lombok.Value;

@Value(staticConstructor = "of")
public class AccountFindByCustomerIdQuery {

  String customerId;
}
