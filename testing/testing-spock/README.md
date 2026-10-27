# Spock Testing Module (`testing-spock`)

This module demonstrates a complete, idiomatic example of using the **Spock Framework** (v2.4 on Groovy 4) with **Spring Boot 4 / Java** and **Maven** to test domain models, repositories, and business services.

The tests in this module exercise the education domain classes from `../../persistence-` (`dk.lundogbendsen.sprinbootcourse.persistence.education`):
- **Models**: `Student`, `Course`, `Teacher`, `AuditedEntity`, `StudentCourseCount`
- **Repositories**: `StudentRepository`, `CourseRepository`, `TeacherRepository` (including custom implementation `TeacherRepositoryCustomImpl`)

---

## Key Spock Features Demonstrated

1. **BDD Structure**: Clear specification blocks (`given:`, `when:`, `then:`, `expect:`, `and:`, `where:`, `cleanup:`).
2. **Power Assertions**: Meaningful failure messages without repetitive assertion libraries (`result.name == "Alice"`).
3. **Data-Driven Testing**: Declarative data tables (`|`, `||`) and data pipes (`<<`) with `@Unroll` for parameterized tests.
4. **Mocking & Interaction Verification**: Built-in mock creation (`Mock()`), stubbing (`>>`), cardinality checks (`1 * repo.save(_)`, `0 * _`), and closure matching.
5. **Exception Testing**: Concise and expressive exception validation using `thrown(ExpectedException)`.
6. **Spring Data JPA Integration**: Testing slices (`@DataJpaTest`) with in-memory H2, JPA auditing, pagination, sorting, projections, and custom JPQL queries.
7. **Full Spring Boot Integration**: End-to-end integration tests (`@SpringBootTest`) with transaction rollback and Spring dependency injection.

---

## Project Structure

```text
testing/testing-spock/
├── pom.xml                                      # Maven configuration (GMavenPlus, Spock 2.4, Groovy 4)
├── README.md                                    # Documentation and guide
├── src
│   ├── main
│   │   ├── java
│   │   │   └── dk/lundogbendsen/springbootcourse/testing/spock/
│   │   │       ├── SpockTestingApplication.java # Spring Boot application entry point
│   │   │       └── service/
│   │   │           ├── EducationService.java    # Business service managing students, courses & teachers
│   │   │           ├── StudentReport.java       # DTO record for aggregated student reports
│   │   │           └── *Exception.java          # Typed domain exceptions (e.g. StudentNotFoundException)
│   │   └── resources
│   │       └── application.properties           # Application configuration
│   └── test
│       ├── groovy
│       │   └── dk/lundogbendsen/springbootcourse/testing/spock/
│       │       ├── unit/
│       │       │   ├── EducationServiceUnitSpec.groovy   # Pure Spock unit testing (Mocks, Stubs, Interactions)
│       │       │   └── StudentGradingDataDrivenSpec.groovy # Data tables, Data pipes, @Unroll
│       │       ├── repository/
│       │       │   ├── StudentRepositoryIntegrationSpec.groovy # @DataJpaTest, JPA Auditing, Pagination
│       │       │   ├── CourseRepositoryIntegrationSpec.groovy  # @DataJpaTest, JPQL, Projections, Relationships
│       │       │   └── TeacherRepositoryIntegrationSpec.groovy # @DataJpaTest, Custom repository fragments
│       │       └── spring/
│       │           └── EducationServiceSpringIntegrationSpec.groovy # @SpringBootTest full context workflow
│       └── resources
│           └── application.properties           # Test datasource and JPA configuration
```

---

## Specification Overview

### 1. `EducationServiceUnitSpec.groovy` (Unit Testing)
- **Focus**: Pure unit testing of business logic without starting a Spring Context.
- **Demonstrates**:
  - Creating mocks: `StudentRepository studentRepository = Mock()`
  - Stubbing return values: `studentRepository.findById(10L) >> Optional.of(student)`
  - Verifying interactions: `1 * courseRepository.save({ Course c -> c.id == 20L }) >> { Course c -> c }`
  - Strict isolation: `0 * _` (no unexpected calls)
  - Exception checking: `when: ... then: thrown(AlreadyEnrolledException)`

### 2. `StudentGradingDataDrivenSpec.groovy` (Data-Driven Testing)
- **Focus**: Testing mathematical calculations, status mappings, and validation boundaries across many input permutations.
- **Demonstrates**:
  - `@Unroll` parameterized test names: `"Total points #totalPoints maps to academic status '#expectedStatus'"`
  - Spock Data Tables:
    ```groovy
    where:
    totalPoints || expectedStatus
    0           || "FAIL"
    10          || "PASS"
    20          || "PASS_WITH_DISTINCTION"
    30          || "HONORS"
    ```
  - Data pipes with `<<` operator and combined matrices.

### 3. `StudentRepositoryIntegrationSpec.groovy` (Data JPA Slice)
- **Focus**: Repository slice testing with `@DataJpaTest`.
- **Demonstrates**:
  - Persisting entities and verifying Spring Data JPA auditing timestamps (`createdDate`, `updatedDate`).
  - Pagination and sorting with `PageRequest.of(0, 2, Sort.by("name").ascending())`.
  - Custom repository delete queries (`deleteByName`).

### 4. `CourseRepositoryIntegrationSpec.groovy` (Relational & Query Slice)
- **Focus**: Testing relational queries and JPQL projections on `CourseRepository`.
- **Demonstrates**:
  - Many-to-Many (`findByStudents`, `findDistinctByStudentsIn`).
  - JPQL name-based lookup (`findCoursesByStudentNames`).
  - Projection into constructor DTO (`StudentCourseCount` via `findTopStudents()`).
  - Range and multi-parameter filtering (`findCoursesByPointsBetween`, `listCourses`).

### 5. `TeacherRepositoryIntegrationSpec.groovy` (Custom Repository Fragment)
- **Focus**: Testing Spring Data JPA custom repository implementations.
- **Demonstrates**:
  - Invoking `TeacherRepositoryCustomImpl.getStudents(teacherName)` executed via `EntityManager`.

### 6. `EducationServiceSpringIntegrationSpec.groovy` (Full Spring Boot Context)
- **Focus**: End-to-end service integration test with `@SpringBootTest`.
- **Demonstrates**:
  - Full Spring dependency injection of `EducationService` and repositories.
  - End-to-end flow: creating entities, establishing relationships, enrolling students, calculating points, and generating reports.
  - `@Transactional` automatic test rollback.

---

## Maven Configuration (`pom.xml`)

The build leverages `gmavenplus-plugin` and `maven-surefire-plugin` to seamlessly compile Groovy specs alongside Java sources:

```xml
<!-- GMavenPlus Plugin compiles Groovy specs under src/test/groovy -->
<plugin>
    <groupId>org.codehaus.gmavenplus</groupId>
    <artifactId>gmavenplus-plugin</artifactId>
    <version>3.0.2</version>
    <executions>
        <execution>
            <goals>
                <goal>addSources</goal>
                <goal>addTestSources</goal>
                <goal>compile</goal>
                <goal>compileTests</goal>
            </goals>
        </execution>
    </executions>
</plugin>

<!-- Surefire executes tests matching *Spec.groovy -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <includes>
            <include>**/*Spec.java</include>
            <include>**/*Spec.groovy</include>
            <include>**/*Test.java</include>
        </includes>
    </configuration>
</plugin>
```

---

## Running the Tests

To run all Spock specifications in this module:
```bash
mvn test
```

To run a single specification:
```bash
mvn test -Dtest=EducationServiceUnitSpec
mvn test -Dtest=StudentGradingDataDrivenSpec
mvn test -Dtest=CourseRepositoryIntegrationSpec
```
