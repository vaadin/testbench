# AI TOOL GUIDANCE

This file provides guidance when working with code in this repository.

## Technology Stack

This is a Vaadin application built with:
- Java
- Spring Boot
- Spring Data JPA with H2 database
- Maven build system

## Development Commands

### Running the Application
```bash
./mvnw                           # Start in development mode (default goal: spring-boot:run)
./mvnw spring-boot:run           # Explicit development mode
```

The application will be available at http://localhost:8080

### Building for Production
```bash
./mvnw -Pproduction package      # Build production JAR
docker build -t my-application:latest .  # Build Docker image
```

### Testing
```bash
./mvnw test                      # Run all tests
./mvnw test -Dtest=TaskServiceTest  # Run a single test class
./mvnw test -Dtest=TaskServiceTest#tasks_are_stored_in_the_database_with_the_current_timestamp  # Run a single test method
```

## Architecture

This project follows a **feature-based package structure** rather than traditional layered architecture. Code is organized by functional units (features), not by technical layers.

### Package Structure

- **`com.example.application.base`**: Reusable components and base classes for all features
  - `base.ui.MainLayout`: AppLayout with drawer navigation using SideNav, automatically populated from @Menu annotations
  - `base.ui.component.ViewToolbar`: Reusable toolbar component for views

- **`com.example.application.examplefeature`**: Example feature demonstrating the structure
  - `Task.java`: JPA entity with validation
  - `TaskRepository.java`: Spring Data JPA repository
  - `TaskService.java`: Service layer with @Transactional methods
  - `ui.TaskListView.java`: Vaadin Flow view component (server-side UI)
  - `TaskServiceTest.java`: Integration test using @SpringBootTest

- **`Application.java`**: Main entry point, annotated with @SpringBootApplication and @Theme("default")

### Key Architecture Patterns

1. **Feature Packages**: Each feature is self-contained with its own UI, business logic, data access, and tests
2. **Navigation**: Views use `@Route` and `@Menu` annotations. MainLayout automatically builds navigation from menu entries
3. **Service Layer**: Use `@Transactional` for write operations and `@Transactional(readOnly = true)` for read operations
4. **Validation**: Domain validation in entity setters (see Task.setDescription)
5. **Dependency Injection**: Constructor injection throughout (no @Autowired on fields)

## Adding New Features

When creating a new feature:
1. Create a new package under `com.example.application` (e.g., `com.example.application.myfeature`)
2. Include: Entity, Repository, Service, and UI view classes
3. Use the `examplefeature` package as a reference
4. Once your features are complete, **delete the `examplefeature` package entirely**

## Vaadin-Specific Notes

- **Server-side rendering**: UI components are Java classes extending Vaadin components
- **Grid lazy loading**: Use `VaadinSpringDataHelpers.toSpringPageRequest(query)` for pagination
- **Themes**: Located in `src/main/frontend/themes/default/`, based on Lumo theme
- **Routing**: `@Route("")` for root path, `@Route("path")` for specific paths
- **Menu**: `@Menu` annotation controls navigation items (order, icon, title)

## Database

- H2 in-memory database for development
- JPA entities use `@GeneratedValue(strategy = GenerationType.SEQUENCE)`
- Entity equality based on ID (see Task.equals/hashCode pattern)

## Testing

- k6 tests are located in `src/test/k6`
- k6 tests can be run with `k6 run src/test/k6/...`
- https://github.com/johannest/k6-demo/blob/main/book-store.js is a good example of a k6 test
- https://grafana.com/docs/k6/latest/ is a good resource for learning k6
