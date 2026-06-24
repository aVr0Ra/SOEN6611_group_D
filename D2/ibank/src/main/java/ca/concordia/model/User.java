package ca.concordia.model;

import ca.concordia.exception.DailyLimitExceededException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {
    private final String cardNumber;
    private String pin;
    private final List<Account> accounts;
    private int tuitionAccountCount;

    private int dailyLimit;
    private int dailyTotal;
    private String firstName;
    private String lastName;
    private String middleName;

    public User(String cardNumber, String pin, int dailyLimit, int dailyTotal,
                String firstName, String lastName, String middleName) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.dailyLimit = dailyLimit;
        this.dailyTotal = dailyTotal;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.accounts = new ArrayList<>();
        this.tuitionAccountCount = 0;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public boolean checkPin(String inputPin) {
        return this.pin.equals(inputPin);
    }
    public boolean changePin(String oldPin, String newPin) {
        if (!checkPin(oldPin)) {
            return false;
        }
        if (newPin == null || !newPin.matches("\\d{4}")) {
            return false;
        }
        this.pin = newPin;
        return true;
    }
    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }
    public boolean addAccount(int balance, AccountType type) {
        if (type == AccountType.TUITION) {
            if (this.tuitionAccountCount >= 1) {
                return false;
            }
            else {
                this.tuitionAccountCount++;
            }
        }

        Account account = new Account(balance, type);
        accounts.add(account);
        return true;
    }
    public Account findAccountByNumber(int accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber() == accountNumber) {
                return account;
            }
        }
        return null;
    }

    public int getDailyLimit() {
        return dailyLimit;
    }
    public void setDailyLimit(int dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public int getDailyTotal() {
        return dailyTotal;
    }
    public void setDailyTotal(int dailyTotal) {
        this.dailyTotal = dailyTotal;
    }
    public void addDailyTotal(int amount) throws DailyLimitExceededException {
        if (this.dailyTotal + amount > this.dailyLimit) {
            throw new DailyLimitExceededException("Daily limit exceeded.");
        }
        this.dailyTotal += amount;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getMiddleName() {
        return middleName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getName() {
        if (middleName == null || middleName.isBlank()) {
            return firstName + " " + lastName;
        }

        return firstName + " " + middleName + " " + lastName;
    }
    public void setName(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
    }
}