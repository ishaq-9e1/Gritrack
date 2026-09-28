# GrievanceTrack

GrievanceTrack is a Spring Boot and MySQL citizen grievance tracking system with category-based department routing, SLA deadlines, escalation tracking and post-closure ratings.

## Tech stack

- Java 17
- Spring Boot 4.1.1
- Spring Data JPA
- MySQL
- HTML, CSS and JavaScript
- Swagger UI / OpenAPI

## Workflow

`SUBMITTED -> IN_PROGRESS -> RESOLVED -> CLOSED`

Each grievance is assigned to a department from its category. The SLA deadline is calculated from the category SLA hours. If an unresolved grievance crosses its deadline, an escalation record is created. A resolution note is required before resolving, and a closed grievance can be rated once from 1 to 5.

## Run

1. Start MySQL and run `database.sql`.
2. Set your local database password:

```bash
export DB_PASSWORD=your_mysql_password
```

3. Start the application:

```bash
mvn spring-boot:run
```

4. Open:

- Application: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
