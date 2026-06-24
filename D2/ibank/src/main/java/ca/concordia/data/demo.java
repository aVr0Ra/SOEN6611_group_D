package ca.concordia.data;

import ca.concordia.model.AccountType;
import ca.concordia.model.User;

public final class demo {
    private demo() {
    }

    public static UserDatabase createDemoDatabase() {
        UserDatabase database = new UserDatabase();

        // User 1: Main student test user
        // PIN: 1234
        // Daily limit: $1000.00, daily used: $0.00
        User student = new User(
                "400000000001",
                "1234",
                100000,
                0,
                "Siming",
                "Yi",
                ""
        );
        student.addAccount(50000, AccountType.CHEQUING);  // Chequing #1: $500.00
        student.addAccount(25000, AccountType.CHEQUING);  // Chequing #2: $250.00
        student.addAccount(30000, AccountType.SAVINGS);   // Savings #1: $300.00
        student.addAccount(80000, AccountType.SAVINGS);   // Savings #2: $800.00
        student.addAccount(150000, AccountType.TUITION);  // Tuition: $1500.00 outstanding
        database.addUser(student);

        // User 2: Daily limit test user
        // PIN: 2222
        // Daily limit: $1000.00, already used: $600.00
        User dailyLimitUser = new User(
                "400000000002",
                "2222",
                100000,
                60000,
                "Adriana",
                "Guevara Contreras",
                "Lilia"
        );
        dailyLimitUser.addAccount(120000, AccountType.CHEQUING); // $1200.00
        dailyLimitUser.addAccount(5000, AccountType.SAVINGS);    // $50.00
        database.addUser(dailyLimitUser);

        // User 3: Low balance test user
        // PIN: 3333
        // Used for insufficient funds test
        User lowBalanceUser = new User(
                "400000000003",
                "3333",
                100000,
                0,
                "Alex",
                "LowBalance",
                ""
        );
        lowBalanceUser.addAccount(1000, AccountType.CHEQUING); // $10.00
        database.addUser(lowBalanceUser);

        return database;
    }
}