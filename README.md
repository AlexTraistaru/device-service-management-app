# Device Service Management App

Spring Boot web application for managing a device service workflow, including customer orders, employees, services, parts, reports, and database integration.

## Overview

This project is a database-driven web application for a device service/repair center.

It supports workflows such as:
- customer registration and login
- device/service order creation and editing
- employee-side order handling
- services and parts management
- reports and administrative views
- invoice-related and order tracking views

The application was developed as an academic project and combines backend business logic, database access, and web UI pages.

For more implementation details, database design information, and project documentation, see the included PDF report: `Device-Service-Management-Report.pdf`.

## Main Features

- **Spring Boot** application structure
- **Database integration** (data access layer for multiple entities)
- **Role-based flows** (client / employee)
- **Order management** (create, edit, assign, update)
- **Services and parts management**
- **Reports / dashboard-like views**
- **Form validation and business rules**
- **Basic security configuration**
- **Unit/integration tests** (project includes test classes)

## Tech Stack

- **Java**
- **Spring Boot**
- **Maven**
- **HTML / CSS** (server-rendered templates)
- **Database integration** (via data access layer / repositories)
- **JUnit / Spring testing** (project tests included)

## Project Structure

- `pom.xml` – Maven project configuration
- `src/main/java/...` – application source code
  - `acces_date/` – data access layer
  - `entitati/` – entity/view model classes
  - `service/` – business logic and validation
  - `securitate/` – security configuration
  - `web/` – controllers (web/API)
- `src/main/resources/`
  - `application.properties` – app configuration
  - `templates/` – HTML templates
  - `static/` – static assets (CSS)
- `src/test/java/...` – tests
- `mvnw`, `mvnw.cmd` – Maven wrapper scripts

## How to Run

### Prerequisites

- Java 
- A configured database instance (matching `application.properties`)
- Maven (optional, since Maven Wrapper is included)

### Run with Maven Wrapper

On Windows:

    mvnw.cmd spring-boot:run

On macOS/Linux:

    ./mvnw spring-boot:run

## Configuration

Before running the project, update `src/main/resources/application.properties` with your local database configuration:
- database URL
- username
- password
- other environment-specific settings

## Notes

- Some package/file/class names and UI text may be in Romanian because the project was developed as part of university coursework.
- The project structure is intentionally preserved to reflect the original academic submission and implementation.

## Documentation

- `Device-Service-Management-Report.pdf` (project report with additional details)

## Author

**Alex Traistaru**  
Student, Automatic Control and Computers (UPB)
