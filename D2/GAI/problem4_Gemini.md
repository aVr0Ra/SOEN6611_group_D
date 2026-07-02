This is an excellent project for SOEN 6611. Designing a simulated, bilingual campus ABM like **iBank Concordia** requires an architecture that prioritizes modularity, readability, and the strict separation of concerns (specifically isolating the UI from the business logic).

For this Java-based implementation, a **Model-View-Controller (MVC)** or Layered Architecture is the most suitable approach. It will ensure your system is testable, maintainable, and easy to explain during your presentation.

Here is a structured implementation plan to guide your development.

---

### 1. Object-Oriented Package & Class Design

To avoid unnecessary complexity while ensuring high cohesion, divide the system into five core packages.

**`com.ibank.model` (Data Entities)**

* `User`: Stores user details (Name, Student ID, Card Number, Encrypted PIN).
* `Account`: Abstract base class with common fields (Account Number, Balance, Account Type).
* `CheckingAccount` & `SavingsAccount`: Subclasses of `Account`.


* `Transaction`: Records transaction history (Date, Type, Amount, Status).

**`com.ibank.service` (Business Logic)**

* `AuthenticationService`: Handles PIN verification, session management, and account locking after consecutive failed attempts.
* `BankService`: Contains core logic for `withdraw()`, `deposit()`, `transfer()`, `checkBalance()`, and `changePin()`.
* `ConcordiaTuitionService`: A specialized service to handle the tuition payment logic (verifying Student ID, checking balance against the university's simulated billing system).
* `LocalizationService`: Manages bilingual (EN/FR) string resolution using Java `ResourceBundle` (`.properties` files for English and French).

**`com.ibank.data` (Data Storage Simulation)**

* `DataStore`: A simulated database using static `HashMap` or `List` collections to store dummy users and accounts. This avoids the overhead of setting up SQL while providing realistic data retrieval.

**`com.ibank.exceptions` (Exception Handling)**

* `InsufficientFundsException`: Triggered during withdrawals, transfers, or tuition payments.
* `AuthenticationFailedException`: Triggered on invalid PIN.
* `AccountLockedException`: Triggered after 3 failed login attempts.
* `DailyLimitExceededException`: Triggered if withdrawal amounts exceed daily ABM limits.

**`com.ibank.gui` (View & Controller)**

* *Recommendation:* Use **JavaFX** with SceneBuilder for a cleaner, more modern declarative UI, or **Java Swing** if your team is more comfortable with it.
* `LoginView`, `MainMenuView`, `TransactionView`, `TuitionView`.
* **Crucial Rule:** The GUI classes must *only* handle user input and display data. They should delegate all logic to the `Service` classes.

---

### 2. System Data Flow

Here is the standard flow of data for a transaction (e.g., Tuition Payment) to demonstrate the decoupled architecture:

1. **View:** The user selects "Pay Tuition" and enters an amount in the `TuitionView`.
2. **Controller:** The GUI captures the event and calls `ConcordiaTuitionService.payTuition(sessionUser, amount)`.
3. **Service:** The service validates the input (checking for negative numbers) and checks the `DataStore` to ensure the `Account` has sufficient funds.
4. **Exception (if applicable):** If funds are low, the service throws an `InsufficientFundsException`. The GUI catches it and asks `LocalizationService` for the correct error message (e.g., *"Fonds insuffisants"* or *"Insufficient funds"*) to display in a modal.
5. **Model/DataStore:** If valid, the service updates the `Account` balance, logs a `Transaction`, and updates the `DataStore`.
6. **View:** The GUI displays a bilingual success message.

---

### 3. Representative Test Scenarios & Data

To satisfy the measurement and testing requirements of SOEN 6611, seed your `DataStore` with specific user profiles designed to trigger normal, boundary, and exceptional cases.

| Scenario Type | Simulated User Data | Test Action | Expected Outcome |
| --- | --- | --- | --- |
| **Normal Use** | User A: $5,000 balance, valid PIN | Withdraw $100; Pay $2,000 tuition | Success; balances update to $4,900 and $2,900; Transaction logged. |
| **Boundary Case** | User B: $500 balance, valid PIN | Withdraw exactly $500 | Success; balance hits $0.00. Edge case handled without errors. |
| **Exceptional (Funds)** | User C: $100 balance, valid PIN | Attempt to transfer $150 | `InsufficientFundsException` thrown; bilingual error message displayed. |
| **Exceptional (Auth)** | User D: Any balance | Enter incorrect PIN 3 times | `AccountLockedException` thrown; card locked; GUI resets to start screen. |
| **Exceptional (Input)** | User E: $1,000 balance | Deposit a negative amount (-$50) | `IllegalArgumentException` thrown; invalid input rejected by service layer. |

---

### 4. Slide-Friendly Architectural Diagrams

To clearly present this architecture to your instructor and TA, I suggest creating two highly scannable diagrams for your slides:

**Diagram 1: The Layered Component Diagram (The "Stack")**

* Create a simple 4-tier block diagram reading from top to bottom.
* **Top Block (Presentation):** "JavaFX/Swing GUI (Bilingual Views)"
* **Middle Block 1 (Controllers/Translation):** "Event Handlers & `ResourceBundle` (EN/FR)"
* **Middle Block 2 (Business Logic):** "Banking Services & Tuition Services"
* **Bottom Block (Persistence):** "In-Memory `DataStore` (HashMaps)"
* *Visual Note:* Draw a strict boundary line between the Presentation and Business Logic layers to emphasize the separation of concerns.

**Diagram 2: Data Flow / Sequence Diagram (The "Action")**

* Use a sequence diagram for a specific feature, like the **Tuition Payment**.
* Show the actors going left to right: `Student (Actor)` $\rightarrow$ `GUI` $\rightarrow$ `TuitionService` $\rightarrow$ `Account Model`.
* Include a "diverging" path (an `alt` block in UML) to visually show the split between a successful payment and an `InsufficientFundsException` being caught and translated for the UI.

---

Does your team have a preference between Java Swing and JavaFX for this project, and would you like a quick boilerplate example of how to cleanly link the view to the service layer in your chosen framework?This is an excellent project for SOEN 6611. Designing a simulated, bilingual campus ABM like **iBank Concordia** requires an architecture that prioritizes modularity, readability, and the strict separation of concerns (specifically isolating the UI from the business logic).

For this Java-based implementation, a **Model-View-Controller (MVC)** or Layered Architecture is the most suitable approach. It will ensure your system is testable, maintainable, and easy to explain during your presentation.

Here is a structured implementation plan to guide your development.

---

### 1. Object-Oriented Package & Class Design

To avoid unnecessary complexity while ensuring high cohesion, divide the system into five core packages.

**`com.ibank.model` (Data Entities)**

* `User`: Stores user details (Name, Student ID, Card Number, Encrypted PIN).
* `Account`: Abstract base class with common fields (Account Number, Balance, Account Type).
* `CheckingAccount` & `SavingsAccount`: Subclasses of `Account`.


* `Transaction`: Records transaction history (Date, Type, Amount, Status).

**`com.ibank.service` (Business Logic)**

* `AuthenticationService`: Handles PIN verification, session management, and account locking after consecutive failed attempts.
* `BankService`: Contains core logic for `withdraw()`, `deposit()`, `transfer()`, `checkBalance()`, and `changePin()`.
* `ConcordiaTuitionService`: A specialized service to handle the tuition payment logic (verifying Student ID, checking balance against the university's simulated billing system).
* `LocalizationService`: Manages bilingual (EN/FR) string resolution using Java `ResourceBundle` (`.properties` files for English and French).

**`com.ibank.data` (Data Storage Simulation)**

* `DataStore`: A simulated database using static `HashMap` or `List` collections to store dummy users and accounts. This avoids the overhead of setting up SQL while providing realistic data retrieval.

**`com.ibank.exceptions` (Exception Handling)**

* `InsufficientFundsException`: Triggered during withdrawals, transfers, or tuition payments.
* `AuthenticationFailedException`: Triggered on invalid PIN.
* `AccountLockedException`: Triggered after 3 failed login attempts.
* `DailyLimitExceededException`: Triggered if withdrawal amounts exceed daily ABM limits.

**`com.ibank.gui` (View & Controller)**

* *Recommendation:* Use **JavaFX** with SceneBuilder for a cleaner, more modern declarative UI, or **Java Swing** if your team is more comfortable with it.
* `LoginView`, `MainMenuView`, `TransactionView`, `TuitionView`.
* **Crucial Rule:** The GUI classes must *only* handle user input and display data. They should delegate all logic to the `Service` classes.

---

### 2. System Data Flow

Here is the standard flow of data for a transaction (e.g., Tuition Payment) to demonstrate the decoupled architecture:

1. **View:** The user selects "Pay Tuition" and enters an amount in the `TuitionView`.
2. **Controller:** The GUI captures the event and calls `ConcordiaTuitionService.payTuition(sessionUser, amount)`.
3. **Service:** The service validates the input (checking for negative numbers) and checks the `DataStore` to ensure the `Account` has sufficient funds.
4. **Exception (if applicable):** If funds are low, the service throws an `InsufficientFundsException`. The GUI catches it and asks `LocalizationService` for the correct error message (e.g., *"Fonds insuffisants"* or *"Insufficient funds"*) to display in a modal.
5. **Model/DataStore:** If valid, the service updates the `Account` balance, logs a `Transaction`, and updates the `DataStore`.
6. **View:** The GUI displays a bilingual success message.

---

### 3. Representative Test Scenarios & Data

To satisfy the measurement and testing requirements of SOEN 6611, seed your `DataStore` with specific user profiles designed to trigger normal, boundary, and exceptional cases.

| Scenario Type | Simulated User Data | Test Action | Expected Outcome |
| --- | --- | --- | --- |
| **Normal Use** | User A: $5,000 balance, valid PIN | Withdraw $100; Pay $2,000 tuition | Success; balances update to $4,900 and $2,900; Transaction logged. |
| **Boundary Case** | User B: $500 balance, valid PIN | Withdraw exactly $500 | Success; balance hits $0.00. Edge case handled without errors. |
| **Exceptional (Funds)** | User C: $100 balance, valid PIN | Attempt to transfer $150 | `InsufficientFundsException` thrown; bilingual error message displayed. |
| **Exceptional (Auth)** | User D: Any balance | Enter incorrect PIN 3 times | `AccountLockedException` thrown; card locked; GUI resets to start screen. |
| **Exceptional (Input)** | User E: $1,000 balance | Deposit a negative amount (-$50) | `IllegalArgumentException` thrown; invalid input rejected by service layer. |

---

### 4. Slide-Friendly Architectural Diagrams

To clearly present this architecture to your instructor and TA, I suggest creating two highly scannable diagrams for your slides:

**Diagram 1: The Layered Component Diagram (The "Stack")**

* Create a simple 4-tier block diagram reading from top to bottom.
* **Top Block (Presentation):** "JavaFX/Swing GUI (Bilingual Views)"
* **Middle Block 1 (Controllers/Translation):** "Event Handlers & `ResourceBundle` (EN/FR)"
* **Middle Block 2 (Business Logic):** "Banking Services & Tuition Services"
* **Bottom Block (Persistence):** "In-Memory `DataStore` (HashMaps)"
* *Visual Note:* Draw a strict boundary line between the Presentation and Business Logic layers to emphasize the separation of concerns.

**Diagram 2: Data Flow / Sequence Diagram (The "Action")**

* Use a sequence diagram for a specific feature, like the **Tuition Payment**.
* Show the actors going left to right: `Student (Actor)` $\rightarrow$ `GUI` $\rightarrow$ `TuitionService` $\rightarrow$ `Account Model`.
* Include a "diverging" path (an `alt` block in UML) to visually show the split between a successful payment and an `InsufficientFundsException` being caught and translated for the UI.

---

Does your team have a preference between Java Swing and JavaFX for this project, and would you like a quick boilerplate example of how to cleanly link the view to the service layer in your chosen framework?