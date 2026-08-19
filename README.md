# Akash Store – Java Swing Billing Software

A desktop billing and inventory management application built with Java 17, Swing, JDBC, and MySQL, following a layered architecture (Model–DAO–Service–UI).

## Features

- **User Authentication** — Secure login with BCrypt password hashing
- **Role-Based Access Control** — Separate Admin and Cashier dashboards with different permissions
- **User Management** — Admin can create new users (Admin/Cashier), and activate/deactivate accounts
- **Change Password** — Any logged-in user can securely update their own password
- **Product Management** — Full CRUD for products (name, category, price, GST %, stock quantity)
- **Customer Management** — Full CRUD for customer records, with phone/email format validation
- **Billing & Invoice Generation** — Cart-based billing screen with live GST and discount calculation
- **Inventory & Stock Management** — Stock automatically reduces on billing and restores on cancellation
- **Sales Dashboard & Reports** — Today's sales, monthly sales, invoice count, top-selling products, low-stock alerts
- **Invoice Cancellation** — Cancels an invoice and restores stock, using a JDBC transaction
- **Input Validation** — Centralized validation for names, emails, phone numbers, prices, GST %, stock, quantities, and discounts
- **JDBC Transactions** — Billing and cancellation operations are atomic (all-or-nothing) using manual commit/rollback
- **JUnit Test Suite** — Unit and integration tests covering calculations and edge cases (insufficient stock, duplicate cancellation, invalid input)

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| UI | Java Swing |
| Database | MySQL 8 |
| Database Access | JDBC (mysql-connector-j) |
| Build Tool | Maven |
| Password Hashing | jBCrypt |
| Testing | JUnit 5 |
| IDE used | Spring Tool Suite (STS) 4 |

## Project Architecture

The project follows a layered architecture, separating concerns across five packages:

```
com.akashstore.billing
├── model     → Plain Java objects mirroring database tables (User, Product, Customer, Invoice, InvoiceItem)
├── dao       → Data Access Objects — the only layer containing SQL/JDBC code
├── service   → Business logic, validation, and calculations (GST, discount, transactions)
├── ui        → Swing screens (JFrame classes) — capture input and display data only
└── util      → Shared helpers (DBConnection, UIStyle, ValidationUtil)
```

**Request flow:** UI → Service → DAO → Database, and back. The UI layer never talks to the database directly; the DAO layer never contains business rules; the Service layer never touches Swing components. This keeps each layer independently testable and easy to modify.

## Database Schema

Five tables, with foreign key relationships enforcing data integrity:

- **users** — login credentials, role (ADMIN/CASHIER), active status
- **customers** — customer name, phone, email
- **products** — name, category, price, GST %, stock quantity
- **invoices** — links to a customer and a user, total amount, discount, payment mode, status (ACTIVE/CANCELLED/RETURNED)
- **invoice_items** — line items per invoice, linking to both `invoices` and `products`

See `database/billing_db_schema.sql` for the full schema script.

## Prerequisites

Before setting up, make sure you have installed:

- **JDK 17** or later
- **MySQL Server 8.x** and MySQL Workbench (or another SQL client)
- **Maven** (bundled with most IDEs, including STS)
- **Spring Tool Suite 4** or any Eclipse-based IDE (or IntelliJ IDEA)

## Setup Instructions

### 1. Clone the repository

```
git clone <your-repository-url>
cd billing-software
```

### 2. Create the database

Open MySQL Workbench (or the MySQL command line) and run the schema script:

```
database/billing_db_schema.sql
```

This creates the `billing_db` database along with all five tables.

### 3. Create the default admin account

Run the following, replacing the value with a bcrypt hash of your chosen password (see note below):

```sql
INSERT INTO users (username, password, role, is_active)
VALUES ('admin', '<bcrypt-hash-of-your-password>', 'ADMIN', TRUE);
```

> **Note:** Passwords are stored as BCrypt hashes, not plain text. If you need to generate a hash for a first-time setup, run a small one-off Java snippet using `BCrypt.hashpw("yourpassword", BCrypt.gensalt())` and paste the output above. Once the app is running, all further password changes and new user creation are handled automatically through the UI — no manual hashing needed after this first setup.

### 4. Configure database credentials

Open `src/main/resources/db.properties` and update it with your local MySQL credentials:

```properties
db.url=jdbc:mysql://localhost:3306/billing_db
db.username=root
db.password=your_mysql_password
```

Credentials are kept in this external file (not hardcoded in Java source) as a security best practice.

### 5. Import into your IDE

- Open Spring Tool Suite (or Eclipse)
- `File → Import → Maven → Existing Maven Projects`
- Select the `billing-software` folder
- Right-click the project → `Maven → Update Project` to download all dependencies (MySQL connector, JUnit, jBCrypt)

### 6. Run the application

Run `com.akashstore.billing.ui.LoginFrame` as a Java Application.

Log in with the admin credentials you created in Step 3.

## Running Tests

Right-click the `com.akashstore.billing.service` package under `src/test/java` → `Run As → JUnit Test`.

This runs the full suite, covering:
- GST and line-total calculation
- Grand total with discount (including the "never goes negative" edge case)
- Empty-cart invoice generation (should throw an exception)
- Insufficient stock during billing (should throw and roll back)
- Cancelling an already-cancelled invoice (should fail safely)
- Product validation (blank name, negative price, negative stock)

## Default Roles

| Role | Access |
|---|---|
| **Admin** | Manage Products, Manage Customers, Manage Users, Sales Reports, Billing, Invoice History, Change Password |
| **Cashier** | New Bill, Change Password |

## Project Status

All core requirements are implemented and tested: authentication, role-based access, product/customer management, billing with GST/discount calculation, inventory management, sales reporting, invoice cancellation with stock restoration, JDBC transactions, and a JUnit test suite.
