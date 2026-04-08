# Demo Web Application (Playwright)

A sample Vaadin application with Spring Boot, demonstrating common patterns like CRUD operations and form handling. Uses Playwright for integration tests instead of TestBench.

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
│   ├── SamplePerson.java
│   └── SamplePersonRepository.java
├── services/                     # Business logic
│   └── SamplePersonService.java
└── views/                        # Vaadin UI views
    ├── MainLayout.java           # Application layout with navigation
    ├── helloworld/
    │   └── HelloWorldView.java   # Simple hello world demo
    └── crudexample/
        └── CrudExampleView.java  # CRUD grid with form editing

src/test/java/com/vaadin/laboratory/views/scenario/
├── HelloWorldPlaywrightIT.java   # E2E test for HelloWorld view
└── CrudExamplePlaywrightIT.java  # E2E test for CRUD view
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

The `scenario` package contains Playwright-based end-to-end tests that simulate real user interactions. These tests extend `AbstractPlaywrightHelper` from the `loadtest-helper` module, which provides:

- Managed Playwright lifecycle (browser, context, page)
- Automatic HAR recording when run via the `loadtest:record-playwright` Maven goal

### Running Tests

```bash
# Run all integration tests (requires the app to be running)
mvn failsafe:integration-test -Dit.test=HelloWorldPlaywrightIT

# Run with the it profile (starts/stops the app automatically)
mvn verify -Pit
```

## Building

```bash
# Development build
mvn package

# Production build with optimized frontend
mvn package -Pproduction
```
