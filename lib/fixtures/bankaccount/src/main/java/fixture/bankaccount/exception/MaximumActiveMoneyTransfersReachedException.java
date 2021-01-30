package fixture.bankaccount.exception;

import java.util.List;

public class MaximumActiveMoneyTransfersReachedException extends RuntimeException {

  public MaximumActiveMoneyTransfersReachedException(int maximumNumberOfActiveTransfers, List<String> activeTransactionIds) {
    super(String.format("Maximum number of active money transfers reached (%d), %s", maximumNumberOfActiveTransfers, activeTransactionIds));
  }
}
