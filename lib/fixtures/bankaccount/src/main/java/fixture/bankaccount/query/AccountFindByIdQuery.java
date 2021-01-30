package fixture.bankaccount.query;

import lombok.Value;

@Value(staticConstructor = "of")
public class AccountFindByIdQuery {

  String accountId;
}
