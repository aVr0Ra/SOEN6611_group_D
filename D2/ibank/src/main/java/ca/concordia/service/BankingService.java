package ca.concordia.service;

import ca.concordia.data.UserDatabase;
import ca.concordia.exception.BalanceException;
import ca.concordia.exception.DailyLimitExceededException;
import ca.concordia.model.Account;
import ca.concordia.model.AccountType;
import ca.concordia.model.Session;
import ca.concordia.model.TransactionResult;
import ca.concordia.model.User;

import java.util.Collections;
import java.util.List;

/**
 * This is the iBank Banking Services API code.
 * It includes 6 services in total, as mentioned in Deliverable 1. (without authenticate user, pls check that in /service/AuthenticateService)
 * withdraw cash, balance inquiry, transfer funds, deposit cheques(cash), change PIN, pay tuition.
 *
 * Pls check each method for detailed usage.
 *
 * ALL BALANCES ARE IN CENTS!!!! doubles have accuracy issues, so the safest way to use is change balance to cents.
 * e.g. balance = 1000 means 1000 cents, which is $10.00
 *
 * @author Siming Yi
 * */
public class BankingService {
    private final static int CASH_BILL_VALUE = 2000; // $20.00 in cents
    private final UserDatabase userDatabase; // User Database

    /**
     * Default constructor
     * */
    public BankingService(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    /**
     * Helper method, to get an account's details from a user in a session. (Originally from User::getAccounts)
     *
     * @param session current session
     * @return a list of accounts, of current session's user.
     * */
    public List<Account> getAccounts(Session session) {
        User user = getUserFromSession(session);

        if (user == null) { // no such user
            return Collections.emptyList();
        }
        return user.getAccounts();
    }

    /**
     * Helper method, to get a user from a session (Originally from userDatabase::findUserByCardNumber)
     *
     * @param session current session
     * @return a User object
     * */
    private User getUserFromSession(Session session) {
        if (session == null || !session.isActive()) {
            return null;
        }

        return userDatabase.findUserByCardNumber(session.getCardNumber());
    }

    /**
     * Helper method, to find an account from a user and accountNumber
     * as a user might have multiple accounts
     *
     * @param accountNumber account number
     * @param user User object
     * @return an Account object
     * */
    private Account findAccount(User user, int accountNumber) {
        for (Account account : user.getAccounts()) {
            if (account.getAccountNumber() == accountNumber) {
                return account;
            }
        }

        return null;
    }

    /**
     * Helper method, to check an account is a regular(chequing or saving) account
     *
     * @param account account object
     * @return true for CHEQUING/SAVING, false for TUITION
     * */
    private boolean isRegularBankAccount(Account account) {
        return account.getAccountType() == AccountType.CHEQUING
                || account.getAccountType() == AccountType.SAVINGS;
    }

    /**
     * Helper method: format an amount to a string
     *
     * @param amount amount
     * @return formatted string
     * */
    private String formatCurrency(int amount) {
        return String.format("$%.2f CAD", amount / 100.0);
    }

    /**
     * Method #1: withdraw.
     * Withdraw cash from an account.
     *
     * @param session current session
     * @param accountNumber account number
     * @param amount withdrawal amount, must be a multiple of $20 bills
     * @return a transaction result object
     * */
    public TransactionResult withdraw(Session session, int accountNumber, int amount) {
        if (amount <= 0) {
            return new TransactionResult(false, "Amount must be positive/Le montant doit être positif.");
        }

        User user = getUserFromSession(session);
        if (user == null) {
            return new TransactionResult(false, "Session is invalid or expired/La session est invalide ou expirée.");
        }

        Account account = findAccount(user, accountNumber);
        if (account == null) {
            return new TransactionResult(false, "Account not found/Compte introuvable.");
        }

        if (!isRegularBankAccount(account)) { // tuition account could only be paid, not withdraw
            return new TransactionResult(false, "Withdrawal is not allowed for tuition account/Le retrait n'est pas autorisé pour le compte de frais de scolarité.");
        }
        if (amount % CASH_BILL_VALUE != 0) {
            return new TransactionResult(false, "Withdrawal amount must be a multiple of $20 CAD/Le montant du retrait doit être un multiple de 20 $ CAD."); // not a multiple of $20
        }
        // this balance check needs to be before the daily limit check
        //otherwise $100 balance with $10000 withdraw could end up with exceeded daily withdrawl limit
        if (amount > account.getBalance()) {
            return new TransactionResult(false, "Insufficient funds/Fonds insuffisants.");
        }

        if (user.getDailyTotal() + amount > user.getDailyLimit()) {
            return new TransactionResult(false, "Withdrawal failed: daily withdrawal limit exceeded/Échec du retrait : limite quotidienne de retrait dépassée.");
        }

        try {
            account.withdraw(amount);
            user.addDailyTotal(amount);
            return new TransactionResult(true, "Withdrawal successful/Retrait réussi", account.getAccountNumber(), account.getBalance());
        } catch (BalanceException e) {
            return new TransactionResult(false, e.getMessage());
        } catch (DailyLimitExceededException e) {
            return new TransactionResult(false, e.getMessage());
        }
    }

    /**
     * Method #2: Balance Inquiry
     * Check an account's balance
     *
     * @param session current session
     * @param accountNumber account number
     * @return a transaction result object
     * */
    public TransactionResult checkBalance(Session session, int accountNumber) {
        User user = getUserFromSession(session);
        if (user == null) {
            return new TransactionResult(false, "Session is invalid or expired/La session est invalide ou expirée.");
        }

        Account account = findAccount(user, accountNumber);
        if (account == null) {
            return new TransactionResult(false, "Account not found/Compte introuvable.");
        }

        return new TransactionResult(true, "Balance inquiry successful/Consultation du solde réussie.", account.getAccountNumber(), account.getBalance());
    }

    /**
     * Method #3: Transfer funds
     * Transfer funds between two different accounts, under the same user
     *
     * @param session current session
     * @param fromAccountNumber from account number
     * @param toAccountNumber to account number
     * @return a transaction result object. If succeeded, it will include the from account's number and balance.
     * */
    public TransactionResult transfer(Session session, int fromAccountNumber, int toAccountNumber, int amount) {
        if (amount <= 0) {
            return new TransactionResult(false, "Amount must be positive/Le montant doit être positif.");
        }

        User user = getUserFromSession(session);
        if (user == null) {
            return new TransactionResult(false, "Session is invalid or expired/La session est invalide ou expirée.");
        }

        if (fromAccountNumber == toAccountNumber) {
            return new TransactionResult(false, "Transfer failed: cannot transfer funds from and to the same account./Échec du virement : impossible de virer des fonds vers le même compte.");
        }

        Account fromAccount = findAccount(user, fromAccountNumber);
        Account toAccount = findAccount(user, toAccountNumber);

        if (fromAccount == null || toAccount == null) {
            return new TransactionResult(false, "Transfer failed: account not found/Échec du virement : compte introuvable.");
        }
        if (!isRegularBankAccount(fromAccount) || !isRegularBankAccount(toAccount)) {
            return new TransactionResult(false, "Transfer is only allowed between chequing and savings accounts/Le virement vers un compte de frais de scolarité n'est pas autorisé.");
        }

        try {
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            return new TransactionResult(true, "Transfer successful/Virement réussi.", fromAccount.getAccountNumber(), fromAccount.getBalance());
        } catch (BalanceException exception) {
            return new TransactionResult(false, exception.getMessage());
        }
    }

    /**
     * Method #4: deposit
     * This method will deposit money to an account
     *
     * @param session current session
     * @param accountNumber account number
     * @param amount amount of money
     * @return a transaction result object
     * */
    public TransactionResult deposit(Session session, int accountNumber, int amount) {
        if (amount <= 0) {
            return new TransactionResult(false, "Amount must be positive./Le montant doit être positif.");
        }

        User user = getUserFromSession(session);
        if (user == null) {
            return new TransactionResult(false, "Session is invalid or expired./La session est invalide ou expirée.");
        }
        Account account = findAccount(user, accountNumber);
        if (account == null) {
            return new TransactionResult(false, "Account not found./Compte introuvable.");
        }
        if (!isRegularBankAccount(account)) {
            return new TransactionResult(false, "Deposit is not allowed for tuition account. / Le dépôt n'est pas autorisé dans un compte de frais de scolarité.");
        }

        try {
            account.deposit(amount);
            return new TransactionResult(true, "Deposit successful./Dépôt réussi.", account.getAccountNumber(), account.getBalance());
        } catch (BalanceException exception) {
            return new TransactionResult(false, exception.getMessage());
        }
    }

    /**
     * Method #5: Change PIN
     *
     * User can change its PIN thru this method
     *
     * @param session current session
     * @param newPin the new PIN
     * @param oldPin the old PIN
     * @return  a transaction result object
     * */
    public TransactionResult changePin(Session session, String oldPin, String newPin) {
        User user = getUserFromSession(session);
        if (user == null) {
            return new TransactionResult(false, "Session is invalid or expired./La session est invalide ou expirée.");
        }
        boolean changed = user.changePin(oldPin, newPin);
        if (!changed) {
            return new TransactionResult(false, "PIN change failed: old PIN is incorrect or new PIN is invalid./L'ancien NIP est incorrect ou le nouveau NIP est invalide.");
        }
        return new TransactionResult(true, "PIN changed successfully/NIP changé avec succès.");
    }




    /**
     * Method #6: Pay Tuition
     *
     * User can use this method to pay tuition.
     * For saving and chequing accounts, +10000 means a deposit of 100.00$ deposit.
     * But for tuition fee account, +10000 means the students own 100.00$ tuition fee to the school (same logic of real Concordia
     * tuition payment system).
     *
     * This is actually just an upgrade for transfer funds method, which cannot transfer money to a tuition account
     *
     * @param session current session
     * @param fromAccountNumber from account number
     * @param tuitionAccountNumber tuition account number
     * @param amount amount of money
     *
     * @return   a transaction result object
     * */
    public TransactionResult payTuition(Session session, int fromAccountNumber, int tuitionAccountNumber, int amount) {
        if (amount <= 0) {
            return new TransactionResult(false, "Amount must be positive/Le montant doit être positif.");
        }

        User user = getUserFromSession(session);
        if (user == null) {
            return new TransactionResult(false, "Session is invalid or expired/La session est invalide ou expirée.");
        }
        if (fromAccountNumber == tuitionAccountNumber) {
            return new TransactionResult(false, "Tuition payment failed: source and tuition accounts cannot be the same/Le compte source et le compte de frais de scolarité ne peuvent pas être identiques.");
        }

        Account fromAccount = findAccount(user, fromAccountNumber);
        Account tuitionAccount = findAccount(user, tuitionAccountNumber);
        if (fromAccount == null) {
            return new TransactionResult(false, "Tuition payment failed: source account not found./Compte source introuvable.");
        }
        if (tuitionAccount == null) {
            return new TransactionResult(false, "Tuition payment failed: tuition account not found./Compte de frais de scolarité introuvable.");
        }
        if (!isRegularBankAccount(fromAccount)) {
            return new TransactionResult(false, "Tuition payment must be made from a chequing or savings account/Le paiement des frais de scolarité doit être effectué à partir d'un compte chèques ou d'un compte d'épargne.");
        }
        if (tuitionAccount.getAccountType() != AccountType.TUITION) {
            return new TransactionResult(false, "Tuition payment target must be a tuition account/Le compte destinataire du paiement doit être un compte de frais de scolarité.");
        }

        if (amount <= 0) {
            return new TransactionResult(false, "Tuition payment amount must be positive/Le montant du paiement des frais de scolarité doit être positif.");
        }
        if (amount > tuitionAccount.getBalance()) {
            return new TransactionResult(false, "Payment amount exceeds outstanding tuition balance/Le montant du paiement dépasse le solde des frais de scolarité à payer.");
        }

        try {
            fromAccount.withdraw(amount);
            tuitionAccount.withdraw(amount);
            return new TransactionResult(true, "Tuition payment successful. Remaining tuition balance/Paiement des frais de scolarité réussi. Solde restant des frais de scolarité : " + String.format("$%.2f CAD", tuitionAccount.getBalance() / 100.0), fromAccount.getAccountNumber(), fromAccount.getBalance());
        } catch (BalanceException exception) {
            return new TransactionResult(false, exception.getMessage());
        }
    }

    /**
     * Helper method: Reset daily withdrawal total
     *
     * This method is used in the simulation to reset the user's daily withdrawal total.
     *
     * @param session current session
     * @return a transaction result object
     */
    public TransactionResult resetDailyWithdrawalTotal(Session session) {
        User user = getUserFromSession(session);
        if (user == null) {
            return new TransactionResult(false, "Session is invalid or expired/La session est invalide ou expirée.");
        }

        user.setDailyTotal(0);

        return new TransactionResult(true, "Daily withdrawal total has been reset/Le total quotidien des retraits a été réinitialisé.");
    }

    /**
     * Helper method: Check daily withdrawal status.
     *
     * @param session current session
     * @return a transaction result object showing daily withdrawal usage
     */
    public TransactionResult checkDailyWithdrawalStatus(Session session) {
        User user = getUserFromSession(session);
        if (user == null) {
            return new TransactionResult(false, "Session is invalid or expired/La session est invalide ou expirée.");
        }

        int remaining = user.getDailyLimit() - user.getDailyTotal();
        String message = "Daily withdrawal used: " + formatCurrency(user.getDailyTotal()) + ". Remaining daily limit: " + formatCurrency(remaining) + ".";

        return new TransactionResult(true, message);
    }
}