# Prompt 1
We are Concordia students preparing for Deliverable 1 for SOEN 6611 project. Problem 3 is “Using the description for iBank, construct a use case model for iBank. This description must be graphical as well as textual, and must include definitions of actors and use cases. ” The output is for professor and TA who expects UML style reasoning, explicit assumptions, and evidence-based explanations.
The System is iBank, a domestic, single bank, on-premises campus ABM for Concordia users in Montreal, Quebec, Canada. It supports CAD transactions, English/French main interface language selection, cash withdrawal, balance inquiry, tuition / fee payment, PIN authentication, receipt generation, and transaction cancellation.
Your task is to review our proposed use case model and suggest improvements, and find weakness or inappropriateness. Like the system boundary is clear etc. Do not add live banking features or legal requirements, focus only on problem 3.
Your output is  expected to answer in these 4 sections: suggested actors, suggested use cases, suggested relationships with reasons, issues or assumptions to clarify. iBank is intended for Concordia context. The use case model must be both graphical and textual. All your outputs will be verified by our group.

# Response 1

## 1. Suggested actors

For your current iBank scope, I would keep the actor set **small and defensible**. Problem 3 asks for actors and use cases, and the note explicitly says there may be different actors and different transactions, but it does **not** require adding many external systems. The deliverable also requires both a graphical and textual use case model. 

### Primary actor

**Concordia User / Bank Client**

This should be the main human actor. It represents a Concordia student, staff member, or other authorized Concordia-campus user who interacts directly with the iBank ABM to perform supported transactions. This fits your system description because iBank is a campus ABM for Concordia users, but the ABM itself is still a banking machine, so the user should also be a bank client.

I suggest naming it:

> **Concordia Bank Client**

This avoids two weaknesses: “Student” is too narrow, because you also said staff; “Customer” is too generic and loses the Concordia context.

### Supporting actors

**Bank Authorization System**

This should be outside the iBank system boundary. It validates the card/account/PIN and confirms whether a requested transaction can proceed. Even if you do not implement a real bank server in D2, it is reasonable in the use case model because authentication and account-related transactions require an external source of account truth.

**Concordia Tuition / Fee System**

This is optional, but I think it is appropriate because you included “tuition / fee payment.” The ABM should not itself decide tuition balances or fee account validity. It should send payment information to a Concordia-related fee system or receive confirmation from it.

**Receipt Printer**

This one depends on how strict you want to be. In UML use case diagrams, hardware devices are often not modeled as actors unless they are external to the software system boundary. Since your system is “iBank” as a product, and the ABM includes a printer physically, you can either omit it as an actor or show it as a supporting actor only if your boundary is the **iBank software**, not the whole ABM machine.

For your slide, I recommend avoiding “Receipt Printer” as an actor unless you explicitly define the boundary as **iBank software running inside the ABM**. Otherwise, it may look like you are putting a component of the system outside the system.

## 2. Suggested use cases

Your use cases should be user-goal level, not button-level. The professor likely expects a use case model that is useful later for Use Case Points in D3, because the course outline includes “Estimation of Effort based on Use Cases” as a course topic, and D3 later asks for UCP effort estimation.  

Recommended use cases inside the **iBank** system boundary:

**Authenticate User**

Validates the user’s identity using card/account information and PIN. This should not be presented as an independent business goal unless your diagram allows it as a required included use case. It is better treated as common behavior needed before account-sensitive transactions.

**Select Interface Language**

Allows the user to select English or French for the main interface. Since you said only the main interface supports English/French, do not imply that every printed message, legal notice, or back-end response is fully bilingual unless your team assumes that.

**Withdraw Cash**

Allows an authenticated user to request a CAD cash withdrawal from an eligible account. This is one of the main transaction use cases.

**View Balance / Balance Inquiry**

Allows an authenticated user to view an account balance in CAD.

**Pay Tuition / Fees**

Allows an authenticated user to pay Concordia tuition or fees in CAD. This should be framed as a campus-specific use case, which helps justify why your ABM is Concordia-oriented rather than a generic ATM.

**Generate Receipt**

Produces a transaction receipt after a completed transaction, when requested or when required by your assumed design. This is usually not a top-level user goal by itself; it is better modeled as optional or included behavior depending on your assumptions.

**Cancel Transaction**

Allows the user to terminate the current transaction before final confirmation. This is cross-cutting behavior and should be described textually, but I would be careful with diagram relationships because UML `extend` arrows can easily become messy.

**End Session / Return Card**

This is not in your feature list, but it is usually necessary for a complete ABM interaction. However, since you asked not to add extra live banking features, you can either include it as part of the postcondition of each transaction or list it as an assumption. I would not add it as a major use case unless your group has already shown it in the diagram.

## 3. Suggested relationships with reasons

Your system boundary should be a rectangle labeled:

> **iBank**

Inside it, put only iBank use cases. Actors stay outside. This is important because the assignment asks for a model “for iBank,” not for the whole banking ecosystem. 

### Recommended associations

**Concordia Bank Client → Select Interface Language**

Reason: the user directly chooses the interface language. This is a user-visible interaction, so an actor association is appropriate.

**Concordia Bank Client → Withdraw Cash / View Balance / Pay Tuition or Fees / Cancel Transaction**

Reason: these are direct goals initiated by the user at the ABM.

**Bank Authorization System ↔ Authenticate User**

Reason: iBank can collect PIN input, but authentication needs validation against account information outside the ABM.

**Bank Authorization System ↔ Withdraw Cash / View Balance / Pay Tuition or Fees**

Reason: these use cases require authorization, account balance retrieval, or transaction approval. Do not draw the bank system connected to every small use case unless it actually participates.

**Concordia Tuition / Fee System ↔ Pay Tuition or Fees**

Reason: the tuition payment is Concordia-specific and likely requires validating a student/account identifier and confirming payment posting.

### Recommended `include`

Use `<<include>>` where the behavior is **mandatory and reused**.

**Withdraw Cash `<<include>>` Authenticate User**

**View Balance `<<include>>` Authenticate User**

**Pay Tuition / Fees `<<include>>` Authenticate User**

Reason: these transactions should not occur without PIN authentication. This is stronger and cleaner than drawing authentication as a vague separate use case.

Possibly:

**Withdraw Cash `<<include>>` Generate Receipt**
**Pay Tuition / Fees `<<include>>` Generate Receipt**

Only use this if your assumption is: “iBank always generates a receipt record or printed receipt for completed financial transactions.” If receipt is optional, do not use `include`.

### Recommended `extend`

Use `<<extend>>` where the behavior is **optional or conditional**.

**Generate Receipt `<<extend>>` Withdraw Cash**

**Generate Receipt `<<extend>>` View Balance**

**Generate Receipt `<<extend>>` Pay Tuition / Fees**

Reason: this is appropriate if the user may choose whether to receive a receipt. In many ABMs, receipt generation is optional, so `extend` is more defensible than `include`.

**Cancel Transaction `<<extend>>` Withdraw Cash / View Balance / Pay Tuition or Fees**

Reason: cancellation occurs only under a condition: the user cancels before confirmation or completion. This is not a required step in every successful transaction, so `extend` is more suitable than `include`.

However, for readability, I would not draw cancellation arrows to every use case if the diagram becomes crowded. You can show one note beside the diagram:

> “Cancel Transaction may extend any active user transaction before final confirmation.”

That is usually clearer.

### Avoid these weak relationships

Do **not** make “PIN Authentication” the parent/general use case of withdrawal, balance inquiry, and payment. Authentication is common required behavior, not a generalization.

Do **not** use `include` for “Cancel Transaction.” Cancellation is not mandatory.

Do **not** put “CAD” as a use case. CAD is a constraint or assumption, not user behavior.

Do **not** put “English” and “French” as separate actors or separate use cases. They are language options within “Select Interface Language.”

Do **not** put “Concordia University” as a broad actor unless a specific Concordia system participates. Use “Concordia Tuition / Fee System” instead.

## 4. Issues or assumptions to clarify

Your biggest possible weakness is **unclear boundary**. Decide whether iBank means the whole physical ABM or only the software. Since the project later requires implementing iBank with a GUI, exception handling, and Java source code, it is probably safer to define the boundary as the **iBank software system** running on a campus ABM. The project description says D2 implementation must be a GUI-based Java product with security and usability concerns, so the software boundary is defensible. 

Clarify these assumptions explicitly on the slide or in the textual description:

**Assumption 1: iBank supports only CAD transactions.**
Reason: the system is domestic and located in Montreal, Canada. Therefore, currency exchange and foreign-currency withdrawal are outside scope.

**Assumption 2: iBank is single-bank.**
Reason: this avoids interbank network use cases such as external bank routing, interbank fees, or third-party account lookup.

**Assumption 3: English/French support applies to the main user interface.**
Reason: this avoids overclaiming full bilingual support for every receipt, error message, back-end response, or legal/administrative content.

**Assumption 4: Tuition / fee payment is limited to Concordia-related payments.**
Reason: otherwise “bill payment” becomes too broad and starts adding generic banking functions.

**Assumption 5: Cancel Transaction is allowed only before final confirmation or completion.**
Reason: after cash is dispensed or payment is posted, cancellation would become reversal/refund behavior, which is outside your stated scope.

**Assumption 6: Receipt generation is either optional or automatic; choose one.**
Reason: this determines whether `Generate Receipt` should be modeled as `extend` or `include`.

My recommended final model would therefore be:

> Actors: Concordia Bank Client, Bank Authorization System, Concordia Tuition / Fee System.
> Main use cases: Select Interface Language, Authenticate User, Withdraw Cash, View Balance, Pay Tuition / Fees, Generate Receipt, Cancel Transaction.
> Key relationships: withdrawal / balance / tuition payment include authentication; receipt extends completed transactions if optional; cancellation extends active transactions before final confirmation.

This is focused, Concordia-specific, and avoids adding unsupported live banking features.

# Prompt 2

Can you draw a use case model for our group

# Response 2

Yes — I drew a clean UML-style use case model for your group.

Download it here: [iBank Use Case Model SVG](sandbox:/mnt/data/ibank_use_case_model.svg)

I used this structure because Problem 3 requires the iBank use case model to be **graphical and textual**, including definitions of actors and use cases, and it explicitly allows different actors/use cases for different transactions.

For your slide, I recommend presenting the diagram with this caption:

> **Figure: Use case model for iBank.** The system boundary is iBank. The primary actor is the Concordia Bank Client. Supporting actors are the Bank Authorization System and the Concordia Tuition / Fee System. Cash withdrawal, balance inquiry, and tuition/fee payment include user authentication. Receipt generation and transaction cancellation are modeled as optional/conditional extensions.

One note: I modeled **Generate Receipt** as `<<extend>>`, assuming the user can choose whether to receive a receipt. If your group assumes a receipt is always generated after each transaction, change it to `<<include>>`.
