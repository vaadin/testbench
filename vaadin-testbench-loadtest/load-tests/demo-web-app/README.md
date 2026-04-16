# Demo Web Application

A sample Vaadin application with Spring Boot, demonstrating common patterns like CRUD operations and form handling.

## Running the Application

```bash
mvn spring-boot:run
```

The application starts at http://localhost:8080

## Project Structure

```
src/main/java/com/vaadin/laboratory/
├── Application.java              # Spring Boot entry point
├── data/                         # JPA entities and repositories
│   ├── AbstractEntity.java       # Base entity with ID and version
│   ├── SamplePerson.java
│   └── SamplePersonRepository.java
├── services/                     # Business logic
│   └── SamplePersonService.java
└── views/                        # Vaadin UI views
    ├── MainLayout.java           # Application layout with navigation
    ├── helloworld/
    │   └── HelloWorldView.java   # Simple hello world demo
    └── crudexample/
        ├── CrudExampleView.java  # Master-detail split layout view
        ├── CrudExampleFactory.java # Factory for CRUD components
        ├── PersonForm.java       # Form component for person editing
        └── PersonGrid.java       # Grid component with lazy loading

src/test/java/com/vaadin/laboratory/views/
├── AbstractIT.java               # Base class for integration tests (proxy-aware)
└── scenario/
    ├── HelloWorldIT.java         # E2E test for HelloWorld view
    └── CrudExampleIT.java        # E2E test for CRUD view
```

## Views

### Hello World
A simple view with a text field and button that displays a greeting notification.

### CRUD Example
A master-detail view with a grid of sample persons and an editing form. Demonstrates:
- Lazy-loading grid with Spring Data
- Form binding with validation
- Create, update, and delete operations

## Integration Tests

The `scenario` package contains TestBench-based end-to-end tests that simulate real user interactions. These tests:

- Verify the application works correctly from a user's perspective
- Can be run as standard integration tests
- Are used by the `demo-web-app-loadtest` module to record user workflows for k6 load testing
