package ca.concordia.model;

public class TransactionResult {
    private final boolean success;
    private final String message;
    private final Integer accountNumber;
    private final Integer balance;

    public TransactionResult(boolean success, String message) {
        this(success, message, null, null);
    }

    public TransactionResult(boolean success, String message, Integer accountNumber, Integer balance) {
        this.success = success;
        this.message = message;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }


    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public Integer getAccountNumber() {
        return accountNumber;
    }
    public Integer getBalance() {
        return balance;
    }
    public boolean hasBalance() {
        return balance != null;
    }

    public String getFormattedBalance() {
        if (balance == null) {
            return "N/A";
        }
        return String.format("$%.2f CAD", balance / 100.0);
    }
}