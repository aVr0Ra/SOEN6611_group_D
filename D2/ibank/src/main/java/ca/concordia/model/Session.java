package ca.concordia.model;

public class Session {
    private final String cardNumber;
    private final String userName;
    private boolean active;

    public Session(String cardNumber, String userName) {
        this.cardNumber = cardNumber;
        this.userName = userName;
        this.active = true;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isActive() {
        return active;
    }

    public void endSession() {
        this.active = false;
    }
}