package ca.concordia.exception;

public class AuthenticationException extends BankingException {
    public AuthenticationException(String message) {
        super(message);
    }
}
