# Monthly Smart Expense Tracker

A separated Java 17 desktop application using Swing, JDBC, and PostgreSQL. It includes registration/login, BCrypt password hashing, income and expense management, weekly/monthly category budgets with warning thresholds, and spending summaries.

## Requirements
- Java 17+
- Maven 3.8+
- PostgreSQL 13+ and a database named `expense_tracker` (or another database URL)

## Configure and run

Create the database, then set connection settings using environment variables:

```sh
createdb expense_tracker
export DB_URL='jdbc:postgresql://localhost:5432/expense_tracker'
export DB_USER='postgres'
export DB_PASSWORD='your-local-database-password'
mvn package
java -jar target/smart-expense-tracker-1.0.0.jar
```

On Windows, set the same variables in PowerShell before running Maven and Java. The schema is created automatically when the application starts. No database password is stored in the source.

## Project structure

- `model/` — User, Category, Transaction, Expense, Income, Budget, and report value objects.
- `data/` — JDBC connection, schema setup, and user/transaction/budget/report repositories.
- `service/` — authentication, transaction validation, and budget calculations.
- `ui/` — Swing login, registration, dashboard, and transaction editor.
- `util/` — currency formatting and application configuration.

## Notes
- Create a personal account from the login window. User records are isolated by user ID.
- Passwords are stored as BCrypt hashes; plaintext passwords are never saved.
- Budget warnings appear when the current-period spend reaches 80% of a limit and when it exceeds the limit.
- Reports summarize the current month by category and show the preceding six months.
- Values are displayed as USD by default. Change the currency symbol in `util/Money.java` if desired.
