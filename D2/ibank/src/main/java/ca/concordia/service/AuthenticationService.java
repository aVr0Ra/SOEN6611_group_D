package ca.concordia.service;

import ca.concordia.data.UserDatabase;
import ca.concordia.exception.AuthenticationException;
import ca.concordia.model.Session;
import ca.concordia.model.User;

/**
 * This is the iBank Authenticate User Services API code.
 * It includes authenticate user method, as mentioned in Deliverable 1.
 * Pls check each method for detailed usage.
 *
 * ALL BALANCES ARE IN CENTS!!!! doubles have accuracy issues, so the safest way to use is change balance to cents.
 * e.g. balance = 1000 means 1000 cents, which is $10.00
 *
 * @author Siming Yi
 * */
public class AuthenticationService {
    private final UserDatabase userDatabase;

    /**
     * Default constructor
     *
     * @param userDatabase user database
     * */
    public AuthenticationService(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    /**
     * Authenticate user method. This method takes card number, and pin to check whether a valid session or not
     *
     * @param cardNumber card number
     * @param pin PIN, should be 4-digit
     * @return a new session, if valid. Or AuthenticationException for invalid authentication
     * */
    public Session authenticateUser(String cardNumber, String pin) throws AuthenticationException {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new AuthenticationException("Card number cannot be empty/Le numéro de carte ne peut pas être vide.");
        }
        if (pin == null || !pin.matches("\\d{4}")) {
            throw new AuthenticationException("PIN must be exactly 4 digits/Le NIP doit contenir exactement 4 chiffres.");
        }

        User user = userDatabase.findUserByCardNumber(cardNumber);
        if (user == null || !user.checkPin(pin)) {
            throw new AuthenticationException("Invalid card number or PIN/Numéro de carte ou NIP invalide.");
        }
        return new Session(user.getCardNumber(), user.getName());
    }

    /**
     * A helper method to end a session.
     *
     * @param session session that needs to be ended
     * */
    public void logout(Session session) {
        if (session != null) {
            session.endSession();
        }
    }
}