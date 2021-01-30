package fixture.bankaccount.exception;

import java.util.function.Supplier;

public class InsufficientBalanceException extends RuntimeException {

  public static Supplier<InsufficientBalanceException> insufficientBalance(Integer currentBalance, Integer requestedAmount) {
    return () -> new InsufficientBalanceException(currentBalance, requestedAmount);
  }

  public InsufficientBalanceException(Integer currentBalance, Integer requestedAmount) {
    super(String.format("Insufficient balance, was:%s, required:%s", currentBalance, requestedAmount));
  }
}
