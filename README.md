# Blood Bank Management System

A desktop application for managing blood bank operations built with JavaFX and Oracle Database. Handles donor registration, blood inventory tracking, transfusion requests, and comprehensive reporting.

## Tech Stack

- **Java 17** with JavaFX 21 for UI
- **Oracle Database** (11g/19c/21c/XE) with Hibernate 6.5 ORM
- **HikariCP** for connection pooling
- **BCrypt** for password hashing
- **Log4j2** for logging
- **Maven** for build management
- **JUnit 5 + Mockito** for testing

## Quick Start

### Prerequisites

1. JDK 17+ installed and `JAVA_HOME` configured
2. Oracle Database XE running on `localhost:1521`
3. Maven 3.9+

### Database Setup

```sql
-- Run as SYSDBA
CREATE USER bloodbank IDENTIFIED BY bloodbank123
DEFAULT TABLESPACE users QUOTA UNLIMITED ON users;

GRANT CONNECT, RESOURCE, CREATE SESSION, CREATE TABLE, 
      CREATE SEQUENCE, CREATE TRIGGER, CREATE PROCEDURE TO bloodbank;
```

Then execute the SQL scripts in order:

```bash
cd sql/
sqlplus bloodbank/bloodbank123@localhost:1521/XE @01_create_tables.sql
sqlplus bloodbank/bloodbank123@localhost:1521/XE @02_insert_seed_data.sql
```

### Run the Application

```bash
mvn clean javafx:run
```

Default login: `admin` / `admin123`

## Project Structure

```
src/main/java/com/bbms/
├── config/              # Hibernate session factory
├── controller/          # JavaFX controllers (Login, Dashboard, Donor, BloodStock)
├── controllers/         # Additional controllers (Recipient, Donations, Reports)
├── model/               # JPA entities (Donor, Recipient, BloodStock, Donation, etc.)
├── dao/                 # Data access layer (GenericDao, AbstractDao, entity DAOs)
├── service/             # Business logic (Auth, Donor, BloodStock, Recipient)
├── util/                # Helpers (BloodCompatibility, PasswordUtil, AlertUtil)
├── BloodBankApp.java    # Main entry point
└── MainApp.java         # Alternate entry point

src/main/resources/
├── fxml/                # UI layouts (10+ views)
├── css/styles.css       # Application styling
├── hibernate.cfg.xml    # Database configuration
├── log4j2.xml           # Logging configuration
└── application.properties

sql/
├── 01_create_tables.sql    # Database schema
└── 02_insert_seed_data.sql # Sample data
```

## Features

**Donor Management** — Register donors, track blood groups, manage donation history with 90-day cooldown enforcement. Age and weight eligibility checks.

**Blood Inventory** — Track stock levels by blood group and component type (Whole Blood, Plasma, Platelets, RBC). Automatic expiry alerts and low stock warnings.

**Recipient Management** — Manage patient records, hospital information, and medical history.

**Blood Requests** — Process blood requests with priority levels (Emergency, High, Normal, Low). Automatic compatibility checking.

**Transfusions** — Record and track blood transfusions with full audit trail.

**Reporting** — Stock summaries, donation trends, request analysis, expiry reports. PDF export support.

## Blood Compatibility Reference

| Recipient | Compatible Donors |
|-----------|-------------------|
| O- | O- |
| O+ | O-, O+ |
| A- | A-, O- |
| A+ | A-, A+, O-, O+ |
| B- | B-, O- |
| B+ | B-, B+, O-, O+ |
| AB- | A-, B-, AB-, O- |
| AB+ | All (Universal Recipient) |

O- is the universal donor. AB+ can receive from anyone.

## Configuration

Edit `src/main/resources/hibernate.cfg.xml`:

```xml
<property name="hibernate.connection.url">jdbc:oracle:thin:@localhost:1521:XE</property>
<property name="hibernate.connection.username">bloodbank</property>
<property name="hibernate.connection.password">bloodbank123</property>
```

Or set environment variables:

```bash
set DB_URL=jdbc:oracle:thin:@localhost:1521:XE
set DB_USER=bloodbank
set DB_PASS=bloodbank123
```

## Building

```bash
# Package as executable JAR
mvn clean package

# Run packaged JAR
java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -jar target/bloodbank-1.0.jar
```

## Testing

```bash
mvn test
```

Integration tests require a running Oracle instance. Configure test database in `src/test/resources/hibernate-test.cfg.xml`.

## Troubleshooting

**ORA-12541: TNS:no listener** — Oracle listener not running. Start with `lsnrctl start`.

**JavaFX runtime components missing** — Ensure JavaFX is in module path or use Maven's javafx plugin.

**Connection refused on 1521** — Check Oracle service is running: `services.msc` → OracleServiceXE.

## Contributing

1. Fork the repo
2. Create feature branch: `git checkout -b feature/your-feature`
3. Commit changes: `git commit -m "Add feature"`
4. Push: `git push origin feature/your-feature`
5. Open a PR

## License

MIT License. See [LICENSE](LICENSE) for details.

## Author

Nivedhaa Sai Saravana Kumar — [@Nivedhaasai](https://github.com/Nivedhaasai)
