# SuperiorBank on Spring Modulith — Let's Build It

We're building the Superior Banki Retail Core with Spring Modulith. The system needs to handle:

- Customer account management
- Domestic and international transfers
- Real-time fraud detection
- Loan lifecycle management
- Multi-channel notifications (SMS, email, push)

### Tech Stack

java 17

Springboot 3.4

### Project Structure

The magic of Spring Modulith starts with your package structure. Every top-level package under your application package is a module — and Spring Modulith enforces this boundary.

```
superiorbank-modulith/
└── src/main/java/com/superior/
    ├── SuperiorBankApplication.java         ← Spring Boot entry point
    │
    ├── account/                         ← MODULE: Account Management
    │   ├── AccountManagementService.java   ← Public API
    │   ├── Account.java                    ← Public API (shared types)
    │   ├── AccountId.java                  ← Public API (value object)
    │   └── internal/                       ← PRIVATE — other modules cannot touch this
    │       ├── AccountRepository.java
    │       ├── AccountValidator.java
    │       ├── BalanceCalculator.java
    │       └── AccountJpaEntity.java
    │
    ├── transaction/                     ← MODULE: Transaction Processing
    │   ├── TransactionService.java         ← Public API
    │   ├── TransferRequest.java            ← Public API
    │   ├── TransactionId.java              ← Public API
    │   └── internal/
    │       ├── TransactionRepository.java
    │       ├── TransactionLimitChecker.java
    │       └── events/
    │           └── TransactionCompletedEvent.java  ← Domain event
    │
    ├── fraud/                           ← MODULE: Fraud Detection
    │   ├── FraudAssessmentService.java     ← Public API
    │   ├── FraudScore.java                 ← Public API
    │   └── internal/
    │       ├── RuleEngine.java
    │       ├── BehavioralAnalyzer.java
    │       ├── FraudCaseRepository.java
    │       └── TransactionFraudListener.java  ← Listens to transaction events
    │
    ├── notification/                    ← MODULE: Customer Notifications
    │   ├── NotificationPreferences.java    ← Public API
    │   └── internal/
    │       ├── EmailNotificationSender.java
    │       ├── SmsNotificationSender.java
    │       ├── PushNotificationSender.java
    │       └── TransactionNotificationListener.java  ← Listens to transaction events
    │
    └── loan/                            ← MODULE: Loan Management
        ├── LoanService.java                ← Public API
        ├── LoanApplication.java            ← Public API
        └── internal/
            ├── LoanRepository.java
            ├── CreditAssessmentEngine.java
            └── AccountLinkedLoanListener.java  ← Listens to account events
```

Modules communicate through events, not through direct dependencies.The `notification` module does not know about `transaction` internals. It only knows that a `TransactionCompletedEvent` was published.

### Local Compile and run

`mvn clean install`

`mvn spring-boot:run`
