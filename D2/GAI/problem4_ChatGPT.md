Below is a structured implementation plan for **Problem 4: Java implementation and testing of iBank Concordia**. The design keeps the system simple enough for a course project while still showing good object-oriented structure, separation of concerns, and testability.

---

# iBank Concordia Implementation Model and Architecture

## 1. Architectural Style

The recommended architecture is a simple **layered Model–View–Controller / Service architecture**.

The goal is to separate the **GUI** from the **banking logic** so that the application is easier to read, test, debug, and maintain.

```text
GUI Layer
  ↓
Controller Layer
  ↓
Service Layer
  ↓
Model + Repository Layer
```

### Main design principle

The GUI should only handle user interaction. It should not directly change account balances or validate banking rules.

For example, the withdrawal screen should not contain logic such as:

```java
if (amount > account.getBalance()) {
    showError("Insufficient funds");
}
```

Instead, the GUI should call:

```java
bankingService.withdraw(userId, accountId, amount);
```

Then the service layer decides whether the transaction is valid.

---

# 2. Proposed Package Structure

A suitable Java package structure could be:

```text
ca.concordia.ibank
│
├── app
│   └── Main.java
│
├── gui
│   ├── LoginFrame.java
│   ├── MainMenuFrame.java
│   ├── WithdrawalPanel.java
│   ├── DepositPanel.java
│   ├── TransferPanel.java
│   ├── BalancePanel.java
│   ├── PinChangePanel.java
│   ├── TuitionPaymentPanel.java
│   └── LanguageManager.java
│
├── controller
│   ├── LoginController.java
│   ├── BankingController.java
│   └── TuitionController.java
│
├── service
│   ├── AuthenticationService.java
│   ├── BankingService.java
│   ├── TuitionPaymentService.java
│   └── TransactionService.java
│
├── model
│   ├── User.java
│   ├── Account.java
│   ├── AccountType.java
│   ├── Transaction.java
│   ├── TransactionType.java
│   ├── TuitionBill.java
│   └── Language.java
│
├── repository
│   ├── UserRepository.java
│   ├── AccountRepository.java
│   ├── TransactionRepository.java
│   ├── TuitionBillRepository.java
│   └── InMemoryDataStore.java
│
├── exception
│   ├── AuthenticationException.java
│   ├── InvalidAmountException.java
│   ├── InsufficientFundsException.java
│   ├── AccountNotFoundException.java
│   ├── InvalidPinException.java
│   ├── TuitionPaymentException.java
│   └── DailyLimitExceededException.java
│
└── util
    ├── MoneyValidator.java
    ├── ReceiptFormatter.java
    └── TestDataLoader.java
```

This structure is simple but still demonstrates good object-oriented design.

---

# 3. Main Layers and Responsibilities

## 3.1 GUI Layer

The GUI can be built using **Java Swing** or **JavaFX**. For this project, **Swing** may be easier because it is built into standard Java and is sufficient for a simulated ABM.

### Main GUI screens

| GUI Class             | Responsibility                                                     |
| --------------------- | ------------------------------------------------------------------ |
| `LoginFrame`          | Allows the user to enter card number, PIN, and language preference |
| `MainMenuFrame`       | Displays the main ABM menu after successful login                  |
| `WithdrawalPanel`     | Handles withdrawal input                                           |
| `DepositPanel`        | Handles deposit input                                              |
| `TransferPanel`       | Handles transfer between accounts                                  |
| `BalancePanel`        | Displays account balances                                          |
| `PinChangePanel`      | Allows the user to change their PIN                                |
| `TuitionPaymentPanel` | Allows students to pay tuition                                     |
| `LanguageManager`     | Provides English/French labels and messages                        |

The GUI should display clear messages such as:

```text
English: Insufficient funds. Please enter a smaller amount.
French: Fonds insuffisants. Veuillez entrer un montant plus petit.
```

The GUI should not directly access the data store. It should communicate with controllers.

---

## 3.2 Controller Layer

Controllers connect the GUI to the service layer.

| Controller          | Responsibility                                                         |
| ------------------- | ---------------------------------------------------------------------- |
| `LoginController`   | Handles login requests from the GUI                                    |
| `BankingController` | Handles withdrawal, deposit, transfer, balance inquiry, and PIN change |
| `TuitionController` | Handles tuition payment requests                                       |

Example responsibility:

```java
public class BankingController {
    private BankingService bankingService;

    public void withdraw(String userId, String accountId, BigDecimal amount) {
        bankingService.withdraw(userId, accountId, amount);
    }
}
```

The controller can catch service exceptions and return user-friendly messages to the GUI.

---

## 3.3 Service Layer

The service layer contains the business logic.

### `AuthenticationService`

Responsibilities:

| Operation                | Description                  |
| ------------------------ | ---------------------------- |
| `login(cardNumber, pin)` | Verifies card number and PIN |
| `logout(userId)`         | Ends the user session        |
| `validatePin(pin)`       | Checks PIN format            |

Rules:

* PIN must be numeric.
* PIN should be 4 digits.
* Account should lock after too many failed login attempts, if this feature is included.
* The system should display bilingual login error messages.

---

### `BankingService`

Responsibilities:

| Operation                                              | Description                           |
| ------------------------------------------------------ | ------------------------------------- |
| `withdraw(userId, accountId, amount)`                  | Withdraws money from selected account |
| `deposit(userId, accountId, amount)`                   | Deposits money into selected account  |
| `transfer(userId, fromAccountId, toAccountId, amount)` | Transfers money between accounts      |
| `getBalance(userId, accountId)`                        | Returns current balance               |
| `changePin(userId, oldPin, newPin)`                    | Changes user PIN                      |

Rules:

* Withdrawal amount must be greater than zero.
* Deposit amount must be greater than zero.
* Transfer amount must be greater than zero.
* Withdrawal and transfer cannot exceed available balance.
* Optional daily withdrawal limit can be included, for example `$500`.
* User can only access accounts that belong to them.
* PIN change requires the old PIN to be correct.
* New PIN must be different from old PIN.
* New PIN must be exactly 4 digits.

Use `BigDecimal` for money values instead of `double`.

---

### `TuitionPaymentService`

Responsibilities:

| Operation                               | Description                        |
| --------------------------------------- | ---------------------------------- |
| `getTuitionBill(studentId)`             | Finds outstanding tuition bill     |
| `payTuition(userId, accountId, amount)` | Pays tuition from selected account |
| `getOutstandingBalance(studentId)`      | Returns unpaid tuition amount      |

Rules:

* Only users with a student ID should be able to pay tuition.
* Tuition payment amount must be greater than zero.
* Payment cannot exceed available bank balance.
* Payment can be partial or full, depending on the design decision.
* Tuition balance should decrease after payment.
* A tuition payment should create a transaction record.

---

## 3.4 Model Layer

The model layer stores the main domain objects.

## `User`

Represents an iBank user.

Important fields:

```java
private String userId;
private String name;
private String cardNumber;
private String pin;
private String studentId;
private Language preferredLanguage;
private List<String> accountIds;
```

Possible methods:

```java
verifyPin(String inputPin)
changePin(String oldPin, String newPin)
hasAccount(String accountId)
```

---

## `Account`

Represents a bank account.

Important fields:

```java
private String accountId;
private String userId;
private AccountType type;
private BigDecimal balance;
```

Possible methods:

```java
deposit(BigDecimal amount)
withdraw(BigDecimal amount)
hasSufficientFunds(BigDecimal amount)
```

---

## `AccountType`

An enum:

```java
public enum AccountType {
    CHEQUING,
    SAVINGS
}
```

---

## `Transaction`

Represents a banking transaction.

Important fields:

```java
private String transactionId;
private String userId;
private String fromAccountId;
private String toAccountId;
private TransactionType type;
private BigDecimal amount;
private LocalDateTime timestamp;
private String description;
```

---

## `TransactionType`

An enum:

```java
public enum TransactionType {
    WITHDRAWAL,
    DEPOSIT,
    TRANSFER,
    BALANCE_INQUIRY,
    PIN_CHANGE,
    TUITION_PAYMENT
}
```

---

## `TuitionBill`

Represents tuition owed by a Concordia student.

Important fields:

```java
private String billId;
private String studentId;
private BigDecimal totalAmount;
private BigDecimal paidAmount;
private LocalDate dueDate;
```

Possible methods:

```java
getOutstandingAmount()
applyPayment(BigDecimal amount)
isPaid()
```

---

## `Language`

An enum:

```java
public enum Language {
    ENGLISH,
    FRENCH
}
```

---

# 4. Data Storage Design

For this project, avoid unnecessary database complexity. Use an **in-memory repository** with representative test data.

A simple `InMemoryDataStore` can store users, accounts, tuition bills, and transactions.

```java
public class InMemoryDataStore {
    private Map<String, User> users = new HashMap<>();
    private Map<String, Account> accounts = new HashMap<>();
    private Map<String, TuitionBill> tuitionBills = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();
}
```

This is suitable for a simulated ABM because:

* It is easy to understand.
* It is easy to test.
* It avoids database setup problems.
* It supports representative test scenarios.

For a more advanced version, file-based storage using CSV or JSON could be added later.

---

# 5. Exception Handling

The system should use custom exceptions to make error handling readable.

| Exception                     | Example Situation                                 | User Message                         |
| ----------------------------- | ------------------------------------------------- | ------------------------------------ |
| `AuthenticationException`     | Wrong card number or PIN                          | Invalid card number or PIN           |
| `InvalidAmountException`      | Amount is zero, negative, or not numeric          | Please enter a valid positive amount |
| `InsufficientFundsException`  | Withdrawal or transfer amount exceeds balance     | Insufficient funds                   |
| `AccountNotFoundException`    | Account does not exist or does not belong to user | Account not found                    |
| `InvalidPinException`         | New PIN is not 4 digits                           | PIN must contain exactly 4 digits    |
| `DailyLimitExceededException` | Withdrawal exceeds daily limit                    | Daily withdrawal limit exceeded      |
| `TuitionPaymentException`     | No tuition bill or invalid tuition payment        | Unable to process tuition payment    |

Recommended approach:

```text
Service layer throws meaningful exceptions.
Controller catches exceptions.
GUI displays bilingual user-friendly messages.
```

Example:

```java
try {
    bankingService.withdraw(userId, accountId, amount);
    view.showSuccess("Withdrawal completed successfully.");
} catch (InsufficientFundsException e) {
    view.showError(languageManager.getMessage("error.insufficientFunds"));
}
```

---

# 6. Bilingual GUI Support

Since iBank is bilingual, avoid hardcoding English text directly inside GUI classes.

Use a `LanguageManager` with message keys.

Example message keys:

```text
login.title
login.cardNumber
login.pin
menu.withdraw
menu.deposit
menu.transfer
menu.balance
menu.changePin
menu.payTuition
error.invalidPin
error.insufficientFunds
success.withdrawal
success.deposit
success.transfer
success.tuitionPayment
```

Example values:

| Key                       | English                               | French                                     |
| ------------------------- | ------------------------------------- | ------------------------------------------ |
| `menu.withdraw`           | Withdraw                              | Retrait                                    |
| `menu.deposit`            | Deposit                               | Dépôt                                      |
| `menu.transfer`           | Transfer                              | Virement                                   |
| `menu.balance`            | Balance Inquiry                       | Consultation du solde                      |
| `menu.changePin`          | Change PIN                            | Changer le NIP                             |
| `menu.payTuition`         | Pay Tuition                           | Payer les frais de scolarité               |
| `error.insufficientFunds` | Insufficient funds.                   | Fonds insuffisants.                        |
| `error.invalidAmount`     | Please enter a valid positive amount. | Veuillez entrer un montant positif valide. |

This improves usability and maintainability.

---

# 7. Data Flow

## Example: Withdrawal

```text
User enters amount in WithdrawalPanel
        ↓
WithdrawalPanel calls BankingController.withdraw(...)
        ↓
BankingController calls BankingService.withdraw(...)
        ↓
BankingService validates:
    - user exists
    - account belongs to user
    - amount is positive
    - account has enough funds
    - daily limit is not exceeded
        ↓
Account balance is updated
        ↓
Transaction is recorded
        ↓
Success or error message is returned to GUI
```

## Example: Tuition Payment

```text
User selects Pay Tuition
        ↓
TuitionPaymentPanel displays outstanding tuition
        ↓
User selects account and payment amount
        ↓
TuitionController calls TuitionPaymentService.payTuition(...)
        ↓
Service validates:
    - user has student ID
    - tuition bill exists
    - amount is positive
    - account has sufficient balance
        ↓
Bank account balance decreases
        ↓
Tuition bill paid amount increases
        ↓
Tuition payment transaction is recorded
        ↓
Receipt message is shown
```

---

# 8. Representative Test Data

Use a small but meaningful data set that covers regular, boundary, and exceptional cases.

## Representative Users

| User ID | Name           | Card Number |  PIN | Student ID | Language |
| ------- | -------------- | ----------: | ---: | ---------- | -------- |
| U001    | Alex Martin    |    40010001 | 1234 | 27384901   | English  |
| U002    | Marie Tremblay |    40010002 | 4321 | 27384902   | French   |
| U003    | Sam Lee        |    40010003 | 1111 | 27384903   | English  |
| U004    | Nora Ahmed     |    40010004 | 2222 | None       | French   |

---

## Representative Accounts

| Account ID | User ID | Type     | Initial Balance |
| ---------- | ------- | -------- | --------------: |
| A001       | U001    | Chequing |       $1,250.00 |
| A002       | U001    | Savings  |       $3,500.00 |
| A003       | U002    | Chequing |          $75.00 |
| A004       | U003    | Chequing |           $0.00 |
| A005       | U004    | Savings  |      $10,000.00 |

---

## Representative Tuition Bills

| Bill ID | Student ID | Total Amount | Paid Amount | Outstanding |
| ------- | ---------- | -----------: | ----------: | ----------: |
| T001    | 27384901   |    $2,000.00 |     $500.00 |   $1,500.00 |
| T002    | 27384902   |      $900.00 |       $0.00 |     $900.00 |
| T003    | 27384903   |    $1,200.00 |   $1,200.00 |       $0.00 |

---

# 9. Representative Test Scenarios

## Authentication Tests

| Test Case          | Input                       | Expected Result                   |
| ------------------ | --------------------------- | --------------------------------- |
| Valid login        | Card `40010001`, PIN `1234` | Login succeeds                    |
| Wrong PIN          | Card `40010001`, PIN `9999` | Error: invalid card number or PIN |
| Unknown card       | Card `99999999`, PIN `1234` | Error: invalid card number or PIN |
| Invalid PIN format | Card `40010001`, PIN `12A4` | Error: PIN must be numeric        |

---

## Withdrawal Tests

| Test Case              | Input                              | Expected Result             |
| ---------------------- | ---------------------------------- | --------------------------- |
| Normal withdrawal      | Withdraw `$100` from A001          | Balance becomes `$1,150.00` |
| Withdraw exact balance | Withdraw `$75` from A003           | Balance becomes `$0.00`     |
| Insufficient funds     | Withdraw `$100` from A003          | Error: insufficient funds   |
| Negative amount        | Withdraw `-$50`                    | Error: invalid amount       |
| Zero amount            | Withdraw `$0`                      | Error: invalid amount       |
| Boundary daily limit   | Withdraw `$500` if limit is `$500` | Transaction succeeds        |
| Exceed daily limit     | Withdraw `$501` if limit is `$500` | Error: daily limit exceeded |

---

## Deposit Tests

| Test Case                         | Input                    | Expected Result             |
| --------------------------------- | ------------------------ | --------------------------- |
| Normal deposit                    | Deposit `$250` into A001 | Balance becomes `$1,500.00` |
| Deposit into zero-balance account | Deposit `$50` into A004  | Balance becomes `$50.00`    |
| Zero deposit                      | Deposit `$0`             | Error: invalid amount       |
| Negative deposit                  | Deposit `-$20`           | Error: invalid amount       |

---

## Transfer Tests

| Test Case                   | Input                                             | Expected Result                                 |
| --------------------------- | ------------------------------------------------- | ----------------------------------------------- |
| Normal transfer             | Transfer `$300` from A001 to A002                 | A001 decreases, A002 increases                  |
| Transfer exact balance      | Transfer `$75` from A003 to another valid account | A003 becomes `$0.00`                            |
| Insufficient funds          | Transfer `$200` from A003                         | Error: insufficient funds                       |
| Invalid destination account | Transfer to unknown account                       | Error: account not found                        |
| Same source and destination | Transfer from A001 to A001                        | Error: source and destination must be different |
| Unauthorized account access | U001 tries to transfer from A003                  | Error: account does not belong to user          |

---

## Balance Inquiry Tests

| Test Case                    | Input                   | Expected Result                        |
| ---------------------------- | ----------------------- | -------------------------------------- |
| Chequing balance             | U001 checks A001        | Shows `$1,250.00`                      |
| Savings balance              | U001 checks A002        | Shows `$3,500.00`                      |
| Zero balance                 | U003 checks A004        | Shows `$0.00`                          |
| Unauthorized balance inquiry | U001 tries to view A003 | Error: account does not belong to user |

---

## PIN Change Tests

| Test Case                | Input                  | Expected Result                  |
| ------------------------ | ---------------------- | -------------------------------- |
| Valid PIN change         | Old `1234`, new `5678` | PIN changes successfully         |
| Wrong old PIN            | Old `9999`, new `5678` | Error: old PIN is incorrect      |
| New PIN too short        | New `123`              | Error: PIN must be 4 digits      |
| New PIN contains letters | New `12A4`             | Error: PIN must be numeric       |
| New PIN same as old PIN  | Old `1234`, new `1234` | Error: new PIN must be different |

---

## Tuition Payment Tests

| Test Case                 | Input                                     | Expected Result                                                |
| ------------------------- | ----------------------------------------- | -------------------------------------------------------------- |
| Partial tuition payment   | U001 pays `$500` toward T001              | Tuition outstanding becomes `$1,000`                           |
| Full tuition payment      | U002 pays `$900` toward T002              | Tuition outstanding becomes `$0`                               |
| No outstanding balance    | U003 tries to pay T003                    | Error: no outstanding tuition                                  |
| Non-student user          | U004 tries to pay tuition                 | Error: no student account found                                |
| Insufficient bank balance | U002 tries to pay `$900` with only `$75`  | Error: insufficient funds                                      |
| Invalid amount            | Pay `$0` or negative amount               | Error: invalid amount                                          |
| Overpayment               | Outstanding is `$900`, user pays `$1,000` | Error or limit payment to `$900`, depending on design decision |

For simplicity and clarity, the recommended behavior is:

```text
Do not allow tuition overpayment.
```

---

# 10. Suggested Class Responsibility Summary

| Class                   | Main Responsibility                                            |
| ----------------------- | -------------------------------------------------------------- |
| `Main`                  | Starts the application                                         |
| `LoginFrame`            | Displays login screen                                          |
| `MainMenuFrame`         | Displays available ABM operations                              |
| `BankingController`     | Receives GUI banking requests                                  |
| `AuthenticationService` | Validates card number and PIN                                  |
| `BankingService`        | Performs withdrawal, deposit, transfer, balance, and PIN logic |
| `TuitionPaymentService` | Handles tuition payment logic                                  |
| `UserRepository`        | Retrieves and updates user data                                |
| `AccountRepository`     | Retrieves and updates account data                             |
| `TransactionRepository` | Stores transaction history                                     |
| `TuitionBillRepository` | Retrieves and updates tuition bills                            |
| `User`                  | Represents an iBank user                                       |
| `Account`               | Represents a bank account                                      |
| `Transaction`           | Represents a transaction record                                |
| `TuitionBill`           | Represents tuition balance                                     |
| `LanguageManager`       | Manages English/French messages                                |

---

# 11. Recommended GUI Navigation

A simple ABM-style flow would be:

```text
Login Screen
    ↓
Language Selection: English / French
    ↓
Main Menu
    ├── Withdraw
    ├── Deposit
    ├── Transfer
    ├── Balance Inquiry
    ├── Change PIN
    ├── Pay Tuition
    └── Logout
```

Each operation screen should include:

```text
Confirm button
Cancel button
Back to Main Menu button
Clear error message area
```

For usability, every successful transaction should display a short receipt-like confirmation:

```text
Transaction successful.
Type: Withdrawal
Amount: $100.00
Account: Chequing A001
Remaining balance: $1,150.00
```

French example:

```text
Transaction réussie.
Type : Retrait
Montant : 100,00 $
Compte : Chèques A001
Solde restant : 1 150,00 $
```

---

# 12. Suggested Component Diagram for Slides

For a slide-friendly component diagram, use five large boxes arranged from top to bottom.

```text
+-----------------------------+
| GUI Layer                   |
| LoginFrame, MainMenuFrame,  |
| Operation Panels            |
+-------------+---------------+
              |
              v
+-----------------------------+
| Controller Layer            |
| LoginController,            |
| BankingController,          |
| TuitionController           |
+-------------+---------------+
              |
              v
+-----------------------------+
| Service Layer               |
| AuthenticationService,      |
| BankingService,             |
| TuitionPaymentService       |
+-------------+---------------+
              |
              v
+-----------------------------+
| Repository Layer            |
| UserRepository,             |
| AccountRepository,          |
| TuitionBillRepository,      |
| TransactionRepository       |
+-------------+---------------+
              |
              v
+-----------------------------+
| Model / Data Layer          |
| User, Account, Transaction, |
| TuitionBill, InMemoryStore  |
+-----------------------------+
```

### Slide label

A good title would be:

```text
iBank Concordia Layered Architecture
```

### Key message for the slide

```text
The GUI is separated from banking logic. Controllers receive user actions, services enforce business rules, repositories manage data access, and models represent banking entities.
```

---

# 13. Suggested Data Flow Diagram for Slides

For the data flow diagram, show one transaction example, such as **tuition payment**, because it combines banking and university-specific functionality.

```text
Student User
    |
    v
TuitionPaymentPanel
    |
    v
TuitionController
    |
    v
TuitionPaymentService
    |
    +------------------> AccountRepository
    |                       |
    |                       v
    |                  Account Balance Updated
    |
    +------------------> TuitionBillRepository
    |                       |
    |                       v
    |                  Tuition Balance Updated
    |
    +------------------> TransactionRepository
                            |
                            v
                       Receipt Generated
```

### Slide label

```text
Tuition Payment Data Flow
```

### Key message for the slide

```text
A tuition payment validates the student, checks available funds, updates both the bank account and tuition bill, records the transaction, and returns a bilingual receipt message.
```

---

# 14. Why This Design Fits the Project

This implementation model is suitable for iBank Concordia because it is:

| Quality Goal      | How the Design Supports It                                       |
| ----------------- | ---------------------------------------------------------------- |
| Usability         | Simple ABM-style GUI with bilingual messages                     |
| Readability       | Clear package structure and class responsibilities               |
| Understandability | Layered design makes the system easy to explain                  |
| Maintainability   | GUI and banking logic are separated                              |
| Testability       | Services can be tested without running the GUI                   |
| Modifiability     | New operations, languages, or storage methods can be added later |

A strong presentation point is that the architecture supports the software measurement qualities mentioned in the project, especially **readability, understandability, usability, and maintainability**.
