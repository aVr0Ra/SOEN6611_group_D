package ca.concordia.exception;

public class DailyLimitExceededException extends BankingException {
    public DailyLimitExceededException(String message) {
        super(message);
    }
}
