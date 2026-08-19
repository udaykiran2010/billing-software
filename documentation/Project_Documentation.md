# Project Documentation — Akash Store Billing Software

**GitHub Repository:** https://github.com/udaykiran2010/billing-software

## Table of Contents
1. Introduction
2. Objective
3. Technology Stack
4. Functional Requirements
5. System Architecture
6. Package Structure
7. Database Design
8. Database Tables
9. JDBC Transaction Management
10. Complete Billing Transaction Flow
11. Complete Invoice Cancellation Flow
12. Database Changes During Operations
13. Validation and Error Handling
14. Security Practices
15. Testing
16. Conclusion

## 1. Introduction

The Billing Management System is a desktop billing application developed using Java Swing, JDBC, and MySQL. It manages the complete billing workflow of a retail store — products, customers, invoices, GST and discount calculation, inventory, invoice cancellation with stock restoration, user roles, and sales reporting.

The system uses a layered architecture where the UI layer communicates with service classes, service classes communicate with DAO (Data Access Object) classes, and DAO classes communicate with the MySQL database using JDBC. The application uses JDBC transactions for billing and invoice cancellation to ensure related database operations complete safely, together, or not at all.

## 2. Objective

The main objectives of the Billing Management System are:
- To provide a role-based, secure billing application (Admin/Cashier)
- To manage products, including price, GST percentage, and stock quantity
- To manage customer records
- To generate invoices with GST, discount, and payment mode
- To automatically reduce stock after a sale
- To support invoice cancellation with automatic stock restoration
- To provide sales reports (today's sales, monthly sales, top-selling products, low-stock alerts)
- To maintain data consistency using JDBC transactions
- To validate all user input centrally and consistently
- To verify core business logic using automated JUnit tests

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
| Development Environment | Spring Tool Suite 4 (Eclipse-based) | IDE used for development |

## 4. Functional Requirements

### 4.1 User Authentication
- Login with username and password
- Passwords hashed using BCrypt (never stored in plain text)
- Admin and Cashier roles, with role-based dashboard routing
- Change password (requires verifying the old password first)
- Account activation and deactivation (deactivated accounts cannot log in)
- Admin can create new users (Add User screen)

### 4.2 Product Management
- Add, update, delete, and view products
- Fields: name, category, price, GST percentage, stock quantity
- Server-side validation: name required, price cannot be negative or unrealistically large, GST between 0–100%, stock cannot be negative

### 4.3 Customer Management
- Add, update, delete, and view customers
- Fields: name, phone, email
- Phone validated as a 10-digit number; email validated by format; both are optional fields

### 4.4 Billing Management
- Product selection via dropdown with live stock/price display
- Cart-based billing (add multiple products with quantity)
- Per-line GST calculation and line total calculation
- Discount applied to the overall bill (cannot exceed the subtotal)
- Payment mode selection (CASH, CARD, UPI)
- Invoice generation, which reduces stock for every item sold

### 4.5 Inventory Management
- Stock automatically reduces when an invoice is generated
- Stock automatically restores when an invoice is cancelled
- Low-stock alert displayed in Sales Reports (products at or below 10 units)
- Insufficient-stock check prevents overselling before an invoice is saved

### 4.6 Invoice Management
- Auto-generated invoice ID
- Invoice status: ACTIVE, CANCELLED, RETURNED
- Invoice History screen listing all invoices with the ability to cancel an ACTIVE invoice
- Already-cancelled invoices cannot be cancelled again

### 4.7 Sales Reports
- Today's total sales
- This month's total sales
- Today's invoice count
- Top 5 selling products by quantity
- Low-stock product list

## 5. System Architecture

The application follows a layered architecture, separating responsibilities into five packages:

```
com.akashstore.billing
├── model     — Data-holding classes matching database tables
├── dao       — Data Access Objects; the only layer containing SQL
├── service   — Business logic, validation rules, and calculations
├── ui        — Swing screens; capture input and display data
└── util      — Shared utilities (DB connection, styling, validation)
```

**Design principle:** each layer only talks to the layer directly below it:
`UI → Service → DAO → Database`

- The UI layer contains no SQL and no business rules
- The DAO layer contains no validation logic or Swing code
- The Service layer contains no direct database connection handling

## 6. Package Structure

**model** — `Product`, `Customer`, `User`, `Invoice`, `InvoiceItem`
**dao** — `ProductDAO`, `CustomerDAO`, `UserDAO`, `InvoiceDAO`, `ReportDAO`
**service** — `ProductService`, `CustomerService`, `AuthService`, `UserService`, `BillingService`, `ReportService`
**ui** — `LoginFrame`, `AdminDashboardFrame`, `CashierDashboardFrame`, `ProductManagementFrame`, `CustomerManagementFrame`, `ManageUsersFrame`, `ChangePasswordFrame`, `BillingFrame`, `InvoiceHistoryFrame`, `ReportsFrame`
**util** — `DBConnection`, `UIStyle`, `ValidationUtil`

## 7. Database Design

The database (`billing_db`) consists of five tables, with foreign key relationships enforcing referential integrity:

- One `user` can create many `invoices` (one-to-many)
- One `customer` can have many `invoices` (one-to-many)
- One `invoice` can contain many `invoice_items` (one-to-many)
- One `product` can appear in many `invoice_items` across different invoices (one-to-many)

`invoice_items` acts as the resolving table between `invoices` and `products`, since a single invoice contains multiple products, and a single product appears across many invoices.

See `database/schema.sql` for the full script and `documentation/ER-Diagram.png` for the visual diagram.

## 8. Database Tables

**users**
| Column | Type | Notes |
|---|---|---|
| user_id | INT, PK, AUTO_INCREMENT | |
| username | VARCHAR(50), UNIQUE, NOT NULL | |
| password | VARCHAR(255), NOT NULL | Stores a BCrypt hash |
| role | ENUM('ADMIN','CASHIER') | |
| is_active | BOOLEAN, DEFAULT TRUE | Deactivated users cannot log in |

**customers**
| Column | Type | Notes |
|---|---|---|
| customer_id | INT, PK, AUTO_INCREMENT | |
| name | VARCHAR(100), NOT NULL | |
| phone | VARCHAR(15) | Optional |
| email | VARCHAR(100) | Optional |

**products**
| Column | Type | Notes |
|---|---|---|
| product_id | INT, PK, AUTO_INCREMENT | |
| name | VARCHAR(100), NOT NULL | |
| category | VARCHAR(50) | |
| price | DECIMAL(10,2), NOT NULL | |
| gst_percentage | DECIMAL(5,2), DEFAULT 0 | |
| stock_quantity | INT, DEFAULT 0 | |

**invoices**
| Column | Type | Notes |
|---|---|---|
| invoice_id | INT, PK, AUTO_INCREMENT | |
| customer_id | INT, FK → customers | Nullable (walk-in customer) |
| user_id | INT, FK → users, NOT NULL | Cashier who created the bill |
| invoice_date | DATETIME, DEFAULT CURRENT_TIMESTAMP | |
| total_amount | DECIMAL(10,2), NOT NULL | |
| discount | DECIMAL(10,2), DEFAULT 0 | |
| payment_mode | VARCHAR(20) | CASH / CARD / UPI |
| status | ENUM('ACTIVE','CANCELLED','RETURNED') | DEFAULT 'ACTIVE' |

**invoice_items**
| Column | Type | Notes |
|---|---|---|
| item_id | INT, PK, AUTO_INCREMENT | |
| invoice_id | INT, FK → invoices, NOT NULL | |
| product_id | INT, FK → products, NOT NULL | |
| quantity | INT, NOT NULL | |
| unit_price | DECIMAL(10,2), NOT NULL | Price at time of sale (not linked live to products.price) |
| gst_amount | DECIMAL(10,2), DEFAULT 0 | |
| line_total | DECIMAL(10,2), NOT NULL | (unit_price × quantity) + gst_amount |

## 9. JDBC Transaction Management

Two operations in this system require multiple related database changes to succeed or fail together: **generating an invoice** and **cancelling an invoice**. Both are implemented as JDBC transactions inside `InvoiceDAO.java`:

- `Connection.setAutoCommit(false)` disables automatic per-statement commits
- All inserts/updates for one operation run using the same `Connection` object
- If any step fails, `Connection.rollback()` undoes every change made in that transaction
- If all steps succeed, `Connection.commit()` saves everything permanently
- `finally` restores `setAutoCommit(true)` and closes the connection

## 10. Complete Billing Transaction Flow

Method: `InvoiceDAO.saveInvoiceWithItems(Invoice invoice, List<InvoiceItem> items)`

1. Open a connection, disable auto-commit
2. Insert one row into `invoices` (customer_id, user_id, total_amount, discount, payment_mode, status = 'ACTIVE'), retrieve the generated `invoice_id`
3. For each item in the cart:
   a. Insert one row into `invoice_items` (invoice_id, product_id, quantity, unit_price, gst_amount, line_total)
   b. Run `UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ? AND stock_quantity >= ?`
   c. If zero rows were updated (meaning stock was insufficient), throw a `SQLException`
4. If every item processed without error, `commit()` — invoice, all items, and all stock reductions are saved permanently
5. If any exception occurred at any point, `rollback()` — the invoice insert, any items already inserted, and any stock already reduced are all undone, as if the billing attempt never happened

This guarantees a bill is never partially saved (for example, an invoice existing with no items, or stock reduced without a matching sale record).

## 11. Complete Invoice Cancellation Flow

Method: `InvoiceDAO.cancelInvoiceAndRestoreStock(int invoiceId)`

1. Open a connection, disable auto-commit
2. Run `UPDATE invoices SET status = 'CANCELLED' WHERE invoice_id = ? AND status = 'ACTIVE'`
3. If zero rows were updated (the invoice was already cancelled, or doesn't exist), throw a `SQLException` — this prevents restoring stock twice for the same invoice
4. Fetch all `invoice_items` for that invoice
5. For each item, run `UPDATE products SET stock_quantity = stock_quantity + ? WHERE product_id = ?`
6. Commit if all steps succeeded; rollback if any step failed

## 12. Database Changes During Operations

**Example — generating a bill for 1 unit of a product priced at ₹2600.00 with 5% GST:**

| Table | Before | After |
|---|---|---|
| products.stock_quantity | 1 | 0 |
| invoices | (no row) | 1 new row, status = ACTIVE, total_amount = 2730.00 |
| invoice_items | (no row) | 1 new row, quantity = 1, gst_amount = 130.00, line_total = 2730.00 |

**Example — cancelling that same invoice:**

| Table | Before | After |
|---|---|---|
| products.stock_quantity | 0 | 1 |
| invoices.status | ACTIVE | CANCELLED |

This exact scenario was manually tested end-to-end during development and verified through both the UI and direct inspection of the `products` table.

## 13. Validation and Error Handling

A centralized `ValidationUtil` class defines reusable rules applied at the service layer before any database write:
- Name: required, under 100 characters
- Email: optional; if provided, must match a standard email pattern
- Phone: optional; if provided, must be a 10-digit number starting with 6–9
- Price: cannot be negative; capped at a realistic maximum
- GST percentage: must be between 0 and 100
- Stock quantity: cannot be negative; capped at a realistic maximum
- Billing quantity: must be between 1 and 1000 per line
- Discount: cannot be negative and cannot exceed the bill subtotal

All DAO methods wrap JDBC calls in try-with-resources blocks and propagate `SQLException` up to the service layer, which catches it, logs it, and returns a failure result to the UI rather than letting the application crash.

## 14. Security Practices

- Database credentials are stored in an external `db.properties` file, not hardcoded in source code
- All SQL queries use `PreparedStatement` with parameterized placeholders (`?`), preventing SQL injection
- Passwords are hashed with BCrypt (`BCrypt.hashpw` / `BCrypt.checkpw`), which includes automatic salting — plain-text passwords are never stored or compared
- Deactivated user accounts are blocked at login, even with correct credentials

## 15. Testing

A JUnit 5 test suite (`BillingServiceTest`, `ProductServiceTest`) covers:
- GST calculation and line-total calculation
- Grand total calculation with discount, including the case where discount exceeds subtotal (result is clamped to zero, never negative)
- Generating an invoice with an empty cart (expected to throw `IllegalArgumentException`)
- Insufficient stock during billing (expected to throw and roll back the transaction)
- Cancelling an already-cancelled invoice (expected to fail safely, without restoring stock twice)
- Product validation: blank name, negative price, negative stock (all expected to be rejected)

Result: 11/11 tests passing. See `screenshots/11_TestCases.png`.

## 16. Conclusion

This project demonstrates a complete, working billing system built with sound software engineering practices — separation of concerns through layered architecture, secure authentication, transactional data integrity for billing and cancellation, centralized input validation, and automated testing covering both normal and edge-case behavior. The modular design allows individual features to be extended without affecting unrelated parts of the system.
