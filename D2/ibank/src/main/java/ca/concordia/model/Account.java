package ca.concordia.model;
import ca.concordia.exception.BalanceException;

public class Account {
    private static int staticAccountNumber = 1001;

    private final AccountType accountType;
    private final int accountNumber;
    private int balance;


    public Account(int balance, AccountType accountType) {
        this.accountType = accountType;
        this.accountNumber = staticAccountNumber ++;
        this.balance = balance;
    }

    public int getAccountNumber() {
        return accountNumber;
    }
    public int getBalance() {
        return balance;
    }
    public void setBalance(int balance) throws BalanceException {
        if (balance < 0) {
            throw new BalanceException("Balance cannot be negative./Le solde ne peut pas être négatif.");
        }
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }
    public void deposit(int amount) throws BalanceException {
        if (amount <= 0) {
            throw new BalanceException("Deposit amount must be positive/Le montant du dépôt doit être positif.");
        }
        balance += amount;
    }
    public void withdraw(int amount) throws BalanceException{
        if (amount <= 0) {
            throw new BalanceException("Withdrawal amount must be positive/Le montant du retrait doit être positif.");
        }
        if (amount > balance) {
            throw new BalanceException("Insufficient funds/Fonds insuffisants.");
        }
        balance -= amount;
    }
}
