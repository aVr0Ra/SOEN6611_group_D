import ca.concordia.data.demo;
import ca.concordia.data.UserDatabase;
import ca.concordia.exception.AuthenticationException;
import ca.concordia.model.Account;
import ca.concordia.model.AccountType;
import ca.concordia.model.Session;
import ca.concordia.model.TransactionResult;
import ca.concordia.service.AuthenticationService;
import ca.concordia.service.BankingService;

import java.util.ArrayList;
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

            List<Integer> chequingAccounts = findAccountNumbers(accounts, AccountType.CHEQUING);
            List<Integer> savingsAccounts = findAccountNumbers(accounts, AccountType.SAVINGS);

            if (chequingAccounts.size() < 2 || savingsAccounts.size() < 2) {
                throw new IllegalStateException("This test requires at least 2 chequing accounts and 2 savings accounts.");
            }

            int chequingAccount1 = chequingAccounts.get(0);
            int chequingAccount2 = chequingAccounts.get(1);
            int savingsAccount1 = savingsAccounts.get(0);
            int savingsAccount2 = savingsAccounts.get(1);
            int tuitionAccount = findAccountNumber(accounts, AccountType.TUITION);

            System.out.println("\nSelected accounts for multi-account test:");
            System.out.println("Chequing #1: " + chequingAccount1);
            System.out.println("Chequing #2: " + chequingAccount2);
            System.out.println("Savings #1: " + savingsAccount1);
            System.out.println("Savings #2: " + savingsAccount2);
            System.out.println("Tuition: " + tuitionAccount);

            printResult("1. Balance inquiry - chequing #1",
                    bankingService.checkBalance(session, chequingAccount1));

            printResult("2. Balance inquiry - chequing #2",
                    bankingService.checkBalance(session, chequingAccount2));

            printResult("3. Balance inquiry - savings #1",
                    bankingService.checkBalance(session, savingsAccount1));

            printResult("4. Balance inquiry - savings #2",
                    bankingService.checkBalance(session, savingsAccount2));

            printResult("5. Deposit $50.25 to savings #1",
                    bankingService.deposit(session, savingsAccount1, 5025));

            printResult("6. Deposit $75.00 to savings #2",
                    bankingService.deposit(session, savingsAccount2, 7500));

            printResult("7. Withdraw $40.00 from chequing #1",
                    bankingService.withdraw(session, chequingAccount1, 4000));

            printResult("8. Withdraw $40.00 from chequing #2",
                    bankingService.withdraw(session, chequingAccount2, 4000));

            printResult("9. Check daily withdrawal status after two withdrawals",
                    bankingService.checkDailyWithdrawalStatus(session));

            printResult("10. Transfer $20.00 from savings #1 to chequing #1",
                    bankingService.transfer(session, savingsAccount1, chequingAccount1, 2000));

            printResult("11. Transfer $50.00 from chequing #2 to savings #2",
                    bankingService.transfer(session, chequingAccount2, savingsAccount2, 5000));

            printResult("12. Check chequing #1 after transfer",
                    bankingService.checkBalance(session, chequingAccount1));

            printResult("13. Check chequing #2 after transfer",
                    bankingService.checkBalance(session, chequingAccount2));

            printResult("14. Check savings #1 after transfer",
                    bankingService.checkBalance(session, savingsAccount1));

            printResult("15. Check savings #2 after transfer",
                    bankingService.checkBalance(session, savingsAccount2));

            printResult("16. Pay tuition $100.00 from chequing #1",
                    bankingService.payTuition(session, chequingAccount1, tuitionAccount, 10000));

            printResult("17. Check tuition balance after payment",
                    bankingService.checkBalance(session, tuitionAccount));

            printResult("18. Change PIN from 1234 to 4321",
                    bankingService.changePin(session, "1234", "4321"));

            authenticationService.logout(session);
            System.out.println("\nLogout completed. Session active? " + session.isActive());

            printResult("19. Try balance inquiry after logout",
                    bankingService.checkBalance(session, chequingAccount1));

            System.out.println("\n===== PIN CHANGE VERIFICATION =====");

            try {
                authenticationService.authenticateUser("400000000001", "1234");
                System.out.println("ERROR: Old PIN still works. This should not happen.");
            } catch (AuthenticationException exception) {
                System.out.println("Old PIN rejected as expected: " + exception.getMessage());
            }

            Session newSession = authenticationService.authenticateUser("400000000001", "4321");
            System.out.println("New PIN works. Login successful for " + newSession.getUserName());

            printResult("20. Reset daily withdrawal total",
                    bankingService.resetDailyWithdrawalTotal(newSession));

            printResult("21. Check daily withdrawal status after reset",
                    bankingService.checkDailyWithdrawalStatus(newSession));

            System.out.println("\n===== ERROR / EXCEPTION BEHAVIOR TESTS =====");

            printResult("Invalid withdrawal amount: $30.00",
                    bankingService.withdraw(newSession, chequingAccount1, 3000));

            printResult("Withdraw from tuition account",
                    bankingService.withdraw(newSession, tuitionAccount, 2000));

            printResult("Deposit to tuition account",
                    bankingService.deposit(newSession, tuitionAccount, 5000));

            printResult("Transfer from chequing to tuition using transfer()",
                    bankingService.transfer(newSession, chequingAccount1, tuitionAccount, 5000));

            printResult("Pay too much tuition",
                    bankingService.payTuition(newSession, chequingAccount1, tuitionAccount, 200000));

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
        return findAccountNumbers(accounts, accountType).get(0);
    }

    private static List<Integer> findAccountNumbers(List<Account> accounts, AccountType accountType) {
        List<Integer> accountNumbers = new ArrayList<>();

        for (Account account : accounts) {
            if (account.getAccountType() == accountType) {
                accountNumbers.add(account.getAccountNumber());
            }
        }

        if (accountNumbers.isEmpty()) {
            throw new IllegalStateException("No account found for type: " + accountType);
        }

        return accountNumbers;
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