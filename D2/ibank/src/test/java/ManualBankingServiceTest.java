
import ca.concordia.data.demo;
import ca.concordia.data.UserDatabase;
import ca.concordia.exception.AuthenticationException;
import ca.concordia.model.Account;
import ca.concordia.model.AccountType;
import ca.concordia.model.Session;
import ca.concordia.model.TransactionResult;
import ca.concordia.service.AuthenticationService;
import ca.concordia.service.BankingService;

import java.util.List;

public class ManualBankingServiceTest {
    public static void main(String[] args) {
        UserDatabase database = demo.createDemoDatabase();

        AuthenticationService authenticationService = new AuthenticationService(database);
        BankingService bankingService = new BankingService(database);

        try {
            System.out.println("===== NORMAL TESTS: MAIN USER =====");

            Session session = authenticationService.authenticateUser("400000000001", "1234");
            System.out.println("Login successful. Welcome, " + session.getUserName());

            List<Account> accounts = bankingService.getAccounts(session);
            printAccounts(accounts);

            int chequingAccount = findAccountNumber(accounts, AccountType.CHEQUING);
            int savingsAccount = findAccountNumber(accounts, AccountType.SAVINGS);
            int tuitionAccount = findAccountNumber(accounts, AccountType.TUITION);

            printResult("1. Balance inquiry - chequing",
                    bankingService.checkBalance(session, chequingAccount));

            printResult("2. Deposit $50.25 to savings",
                    bankingService.deposit(session, savingsAccount, 5025));

            printResult("3. Withdraw $40.00 from chequing",
                    bankingService.withdraw(session, chequingAccount, 4000));

            printResult("4. Check daily withdrawal status",
                    bankingService.checkDailyWithdrawalStatus(session));

            printResult("5. Transfer $20.00 from savings to chequing",
                    bankingService.transfer(session, savingsAccount, chequingAccount, 2000));

            printResult("6. Pay tuition $100.00 from chequing",
                    bankingService.payTuition(session, chequingAccount, tuitionAccount, 10000));

            printResult("7. Change PIN from 1234 to 4321",
                    bankingService.changePin(session, "1234", "4321"));

            authenticationService.logout(session);
            System.out.println("\nLogout completed. Session active? " + session.isActive());

            System.out.println("\n===== PIN CHANGE VERIFICATION =====");

            try {
                authenticationService.authenticateUser("400000000001", "1234");
                System.out.println("ERROR: Old PIN still works. This should not happen.");
            } catch (AuthenticationException exception) {
                System.out.println("Old PIN rejected as expected: " + exception.getMessage());
            }

            Session newSession = authenticationService.authenticateUser("400000000001", "4321");
            System.out.println("New PIN works. Login successful for " + newSession.getUserName());

            printResult("8. Reset daily withdrawal total",
                    bankingService.resetDailyWithdrawalTotal(newSession));

            printResult("9. Check daily withdrawal status after reset",
                    bankingService.checkDailyWithdrawalStatus(newSession));

            System.out.println("\n===== ERROR / EXCEPTION BEHAVIOR TESTS =====");

            printResult("Invalid withdrawal amount: $30.00",
                    bankingService.withdraw(newSession, chequingAccount, 3000));

            printResult("Withdraw from tuition account",
                    bankingService.withdraw(newSession, tuitionAccount, 2000));

            printResult("Deposit to tuition account",
                    bankingService.deposit(newSession, tuitionAccount, 5000));

            printResult("Transfer from chequing to tuition using transfer()",
                    bankingService.transfer(newSession, chequingAccount, tuitionAccount, 5000));

            printResult("Pay too much tuition",
                    bankingService.payTuition(newSession, chequingAccount, tuitionAccount, 200000));

            System.out.println("\n===== DAILY LIMIT USER TEST =====");

            Session dailyLimitSession = authenticationService.authenticateUser("400000000002", "2222");
            int dailyLimitChequing = findAccountNumber(
                    bankingService.getAccounts(dailyLimitSession),
                    AccountType.CHEQUING
            );

            printResult("Daily limit exceeded test",
                    bankingService.withdraw(dailyLimitSession, dailyLimitChequing, 60000));

            System.out.println("\n===== LOW BALANCE USER TEST =====");

            Session lowBalanceSession = authenticationService.authenticateUser("400000000003", "3333");
            int lowBalanceChequing = findAccountNumber(
                    bankingService.getAccounts(lowBalanceSession),
                    AccountType.CHEQUING
            );

            printResult("Insufficient funds test",
                    bankingService.withdraw(lowBalanceSession, lowBalanceChequing, 2000));

        } catch (AuthenticationException exception) {
            System.out.println("Authentication failed: " + exception.getMessage());
        }
    }

    private static int findAccountNumber(List<Account> accounts, AccountType accountType) {
        for (Account account : accounts) {
            if (account.getAccountType() == accountType) {
                return account.getAccountNumber();
            }
        }

        throw new IllegalStateException("No account found for type: " + accountType);
    }

    private static void printAccounts(List<Account> accounts) {
        System.out.println("\nAccounts:");
        for (Account account : accounts) {
            System.out.printf(
                    "Account %d | Type: %s | Balance: $%.2f CAD%n",
                    account.getAccountNumber(),
                    account.getAccountType(),
                    account.getBalance() / 100.0
            );
        }
    }

    private static void printResult(String title, TransactionResult result) {
        System.out.println("\n--- " + title + " ---");
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Message: " + result.getMessage());

        if (result.getAccountNumber() != null) {
            System.out.println("Account: " + result.getAccountNumber());
        }

        if (result.hasBalance()) {
            System.out.println("Balance: " + result.getFormattedBalance());
        }
    }
}