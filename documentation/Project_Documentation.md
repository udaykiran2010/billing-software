# Project Documentation — Akash Store Billing Software

## 1. Introduction

This project is a desktop-based billing and inventory management system built for a retail store. It replicates the real-world workflow of a point-of-sale (POS) counter: staff log in with role-based access, manage products and customers, generate invoices with GST and discount calculations, track inventory, and view sales reports.

## 2. Objective

To build a functional, secure, and well-structured billing application that demonstrates:
- Layered software architecture (Model–DAO–Service–UI)
- Relational database design with proper normalization
- Secure authentication practices
- Transactional data integrity for financial operations
- Automated testing of business logic

## 3. Technology Stack

| Component | Technology | Reason for Choice |
|---|---|---|
| Language | Java 17 | Modern, stable, long-term support version |
| UI Framework | Java Swing | Built-in Java toolkit for desktop applications; suitable for an offline billing counter |
| Database | MySQL 8 | Reliable open-source relational database with strong transaction support |
| Database Connectivity | JDBC (mysql-connector-j) | Standard Java API for relational database access |
| Build Tool | Maven | Manages dependencies and standardizes project structure |
| Password Security | jBCrypt | Industry-standard adaptive hashing algorithm with built-in salting |
| Testing | JUnit 5 | Standard Java unit testing framework |

## 4. System Architecture

The application follows a **layered architecture**, separating responsibilities into five packages:

```
com.akashstore.billing
├── model     — Data-holding classes matching database tables
├── dao       — Data Access Objects; the only layer containing SQL
├── service   — Business logic, validation rules, and calculations
├── ui        — Swing screens; capture input and display data
└── util      — Shared utilities (DB connection, styling, validation)
```

**Design principle:** each layer only talks to the layer directly below it.
`UI → Service → DAO → Database`

This separation means:
- The UI layer contains no SQL and no business rules
- The DAO layer contains no validation logic or Swing code
- The Service layer contains no direct database connection handling
- Any layer can be modified or replaced independently (e.g., swapping Swing for a web UI would only require rewriting the `ui` package)

## 5. Database Design

The database consists of five tables:

- **users** — stores login credentials (BCrypt-hashed passwords), role (ADMIN/CASHIER), and active status
- **customers** — stores customer contact details
- **products** — stores the product catalog including price, GST percentage, and stock quantity
- **invoices** — stores the invoice header: which customer, which user (cashier), total, discount, payment mode, and status
- **invoice_items** — stores individual line items per invoice, linking invoices to products (resolves the many-to-many relationship between invoices and products)

**Relationships:**
- One `user` can create many `invoices` (one-to-many)
- One `customer` can have many `invoices` (one-to-many)
- One `invoice` can contain many `invoice_items` (one-to-many)
- One `product` can appear in many `invoice_items` across different invoices (one-to-many)

See `database/billing_db_schema.sql` for the full script, and `documentation/ER_Diagram.png` for the visual diagram.

## 6. Key Features and Implementation Notes

### 6.1 Authentication and Authorization
Passwords are hashed using BCrypt before storage — plain-text passwords are never saved. On login, the entered password is verified against the stored hash using `BCrypt.checkpw()`. After successful login, the user's `role` field determines whether they are routed to the Admin Dashboard or Cashier Dashboard.

### 6.2 Billing and JDBC Transactions
Generating an invoice requires three related database operations to succeed together: inserting the invoice header, inserting each invoice line item, and reducing stock for each product sold. This is implemented as a single JDBC transaction:
- `Connection.setAutoCommit(false)` disables automatic per-statement commits
- All inserts and updates run using the same `Connection` object
- If any step fails (e.g., insufficient stock), `Connection.rollback()` undoes every change made in that transaction
- If all steps succeed, `Connection.commit()` saves everything permanently

The same pattern is used for invoice cancellation, which restores stock and updates invoice status together.

### 6.3 Input Validation
A centralized `ValidationUtil` class defines rules for names, email format, phone number format, price ranges, GST percentage bounds, stock quantity limits, billing quantity limits, and discount bounds (a discount cannot exceed the bill subtotal). This keeps validation logic in one reusable place rather than duplicated across screens.

### 6.4 Security Practices
- Database credentials are stored in an external `db.properties` file, not hardcoded in source code
- All SQL queries use `PreparedStatement` with parameterized placeholders (`?`), preventing SQL injection
- Passwords are hashed with BCrypt, which includes automatic salting

## 7. Testing

A JUnit 5 test suite covers:
- Pure calculation logic (GST calculation, line totals, grand total with discount)
- Boundary conditions (discount larger than subtotal should not go negative)
- Exception handling (generating an invoice with an empty cart)
- Integration-level edge cases against the real database: insufficient stock during billing, and preventing a double-cancellation of the same invoice
- Product validation rules (blank name, negative price, negative stock)

See `test-results/` for a screenshot of the test run results.

## 8. Conclusion

This project demonstrates a complete, working billing system built with sound software engineering practices — separation of concerns through layered architecture, secure authentication, transactional data integrity, input validation, and automated testing. The modular design allows individual features (e.g., reporting, user management) to be extended without affecting unrelated parts of the system.