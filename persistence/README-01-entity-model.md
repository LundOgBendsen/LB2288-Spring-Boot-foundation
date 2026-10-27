# Persistence

Solutions for these exercises are in [SOLUTIONS-01-entity-model.md](SOLUTIONS-01-entity-model.md).

## Exercises for Persistence

Prerequisite for these exercises is a new Springboot project with the following dependencies:

- spring-boot-starter-data-jpa
- h2 database
- lombok

### Exercise 1: create a Domain model for the Education case

In this exercise you are going to create 3 domain classes representing Student, Course and Teacher.

A Student has a name and a set of Courses.
A Course has a subject, some points, a set of Students and a Teacher.
A Teacher has a name and a set of Courses.

- Create the 3 domain classes. Use Lombok annotations to define getters and setters etc.
```
//Lombok annotations
@Getter <-- very important
@Setter <-- very important
@Builder <-- nice to have
@NoArgsConstructor <-- very important
@AllArgsConstructor <-- nice to have
```

### Exercise 2: Upgrade the Domain model to an Entity model

- Update the 3 domain classes to Entities by adding the @Entity annotation and a Long id annotated with @Id and
  @GeneratedValue.
- Add appropriate relationship annotations on the entities.
- Hint: The relations between Student and Course are BI-DIRECTIONAL. Likewise are relations between Teacher and Course.
  This means that one of the entities "give up" their end of the relation using the "mappedby" option in the relational
  annotation.

### Exercise 3: Make repositories for the entity model

- Make 3 repository interfaces named after the 3 entities extending the JpaRepository interface.

### Exercise 4: Enable log of the DDL generated

At this point we have an Entity Model, and we now would like to see what DDL is generated. Also notice that Hibernate
understands dependencies between tables, and will drop tables "bottoms-up", and create them "top-down".

In order to see the SQL output, add this line to your application configuration:

`spring.jpa.show-sql=true`

This is very nice in the development phase on your local machine where you make a lot of changes. But when the Entity
model starts stabilizing or when you deploy to a test/prod environment, you probably don't want the application to wipe
the database on every start.

You can set **spring.jpa.hibernate.ddl-auto** explicitly and the standard Hibernate property values are _none, validate,
update, create-drop_. Spring Boot chooses a default value for you based on whether it thinks **your database is
embedded (default create-drop)** or not (default none).
An embedded database is detected by looking at the Connection type: hsqldb, h2 and derby are embedded, the rest are not.
Be careful when switching from in-memory to a “real” database that you don’t make assumptions about the existence of the
tables and data in the new platform. You either have to set ddl-auto explicitly, or use one of the other mechanisms to
initialize the database.

### Exercise 5: Make unit tests (@DataJpaTest) to try out the repositories

- Make a new test class for testing the repositories
- Add the three repositories using @Autowired
- Also add an EntityManager - this is necessary when running unit test to be able to force Hibernate to remove entities
  from memory and reload them upon request.
  Otherwise entity relations are not hydrated.
  Use `@Autowired EntityManager em;`
- Define the following entities:

```java
    //Notice the use of the fluent builder().....build() pattern provided by the Lombok @Builder
    final Student josh = Student.builder().name("Josh").build();
    final Student jane = Student.builder().name("Jane").build();
    final Student tom = Student.builder().name("Tom").build();
    final Student anna = Student.builder().name("Anna").build();

    final Teacher johnson = Teacher.builder().name("Johnson").build();
    final Teacher smith = Teacher.builder().name("Smith").build();
    final Teacher kayne = Teacher.builder().name("Kayne").build();

    final Course art = Course.builder().subject("art").points(5)
            .teacher(johnson)
            .students(Set.of(josh, jane, tom)).build();
    final Course philosophy = Course.builder().subject("philosophy").points(10)
            .teacher(smith)
            .students(Set.of(tom, anna, jane)).build();
    final Course math = Course.builder().subject("math").points(15)
            .teacher(kayne)
            .students(Set.of(josh, tom, anna)).build();
```

- Make a test method that saves the entities using the repositores.
- use courseRepository to flush pending database operations.
- Use entityManager to clear the loaded entities, so they will be reloaded in the following test, and thereby
  re-hydrated.
- Run the test and verify that it succeeds.

### Exercise 6: Convert test to @BeforeEach

The @Test in previous exercise was just for warming up. To see if we setup the entities right.
In this exercise you'll convert the test to an initializing method that runs before every test.

- Change the annotation to @BeforeEach
- Change the name to init ().

By doing this you now have a solid, well-known data foundation for coming tests.
