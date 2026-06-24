package ca.concordia.data;

import ca.concordia.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private final Map<String, User> users;

    public UserDatabase() {
        this.users = new HashMap<>();
    }

    public void addUser(User user) {
        users.put(user.getCardNumber(), user);
    }

    public User findUserByCardNumber(String cardNumber) {
        return users.get(cardNumber);
    }
}