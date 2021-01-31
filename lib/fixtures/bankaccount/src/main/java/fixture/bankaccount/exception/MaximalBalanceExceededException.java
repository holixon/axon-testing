package fixture.bankaccount.exception;

import java.util.function.Supplier;

public class MaximalBalanceExceededException extends RuntimeException {

  public static Supplier<MaximalBalanceExceededException> maximalBalanceException(Integer currentBalance, Integer requestedAmount,
                                                                                  Integer maximalBalance) {
    return () -> new MaximalBalanceExceededException(currentBalance, requestedAmount, maximalBalance);
  }

  public MaximalBalanceExceededException(Integer currentBalance, Integer requestedAmount, Integer maximalBalance) {
    super(
      String.format("MaximalBalance exceeded: currentBalance=%s, amount=%s, maximalBalance=%s", currentBalance, requestedAmount, maximalBalance)
    );
  }
}
