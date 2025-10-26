# Persistence

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

#### Solution
```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private String subject;
    private Integer points;
    private Teacher teacher;
    private Set<Student> students;
}
```

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private String name;
    private Set<Course> courses;
}
```

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    private String name;
    private Set<Course> teaches;
}
```

### Exercise 2: Upgrade the Domain model to an Entity model

- Update the 3 domain classes to Entities by adding the @Entity annotation and a Long id annotated with @Id and @GeneratedValue.
- Add appropriate relationship annotations on the entities.
- Hint: The relations between Student and Course are BI-DIRECTIONAL. Likewise are relations between Teacher and Course. 
  This means that one of the entities "give up" their end of the relation using the "mappedby" option in the relational annotation.


#### Solution
```java
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue
    private Long id;
    private String subject;
    private Integer points;
    @ManyToOne
    private Teacher teacher;
    @ManyToMany
    private Set<Student> students;
}
```

```java
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "students")
    private Set<Course> courses;
}
```

```java
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
  @Id
  @GeneratedValue
  private Long id;
  private String name;
  @OneToMany(mappedBy = "teacher")
  private Set<Course> teaches;
}
```

### Exercise 3: Make repositories for the entity model

- Make 3 repository interfaces named after the 3 entities extending the JpaRepository interface.

#### Solution
```java
public interface CourseRepository extends JpaRepository<Course, Long> {}
```
```java
public interface StudentRepository extends JpaRepository<Student, Long> {}
```
```java
public interface TeacherRepository extends JpaRepository<Teacher, Long> {}
```

### Exercise 4: Run the application and see the DDL generated
At this point we have an Entity Model, and we now would like to see what DDL is generated. Also notice that Hibernate understands dependencies between tables, and will drop tables "bottoms-up", and create them "top-down".

In order to see the SQL output, add this line to your application configuration:

`spring.jpa.show-sql=true`

Then run the application and inspect the logging. Notice how it starts out dropping any table if it exists. It cleans everything up and starts out fresh.
This is very nice in the development phase on your local machine where you make a lot of changes. But when the Entity model starts stabilizing or when you deploy to a test/prod environment, you probably don't want the application to wipe the database on every start. 

You can set **spring.jpa.hibernate.ddl-auto** explicitly and the standard Hibernate property values are _none, validate, update, create-drop_. Spring Boot chooses a default value for you based on whether it thinks **your database is embedded (default create-drop)** or not (default none). 
An embedded database is detected by looking at the Connection type: hsqldb, h2 and derby are embedded, the rest are not. 
Be careful when switching from in-memory to a “real” database that you don’t make assumptions about the existence of the tables and data in the new platform. You either have to set ddl-auto explicitly, or use one of the other mechanisms to initialize the database.


#### Solution
```sql
Hibernate: drop table if exists course CASCADE 
Hibernate: drop table if exists course_students CASCADE 
Hibernate: drop table if exists student CASCADE 
Hibernate: drop table if exists teacher CASCADE 
Hibernate: drop sequence if exists hibernate_sequence
Hibernate: create sequence hibernate_sequence start with 1 increment by 1
Hibernate: create table course (id bigint not null, points integer, subject varchar(255), teacher_id bigint, primary key (id))
Hibernate: create table course_students (courses_id bigint not null, students_id bigint not null)
Hibernate: create table student (id bigint not null, name varchar(255), primary key (id))
Hibernate: create table teacher (id bigint not null, name varchar(255), primary key (id))
Hibernate: alter table course add constraint FKsybhlxoejr4j3teomm5u2bx1n foreign key (teacher_id) references teacher
Hibernate: alter table course_students add constraint FK532dg5ikp3dvbrbiiqefdoe6m foreign key (students_id) references student
Hibernate: alter table course_students add constraint FKh6irfl8rj4jgb3xrlyxsr2bdk foreign key (courses_id) references course
```


### Exercise 5: Make unit tests (@DataJpaTest) to try out the repositories
- Make a new test class for testing the repositories
- Add the three repositories using @Autowired
- Also add an EntityManager - this is necessary when running unit test to be able to force Hibernate to remove entities from memory and reload them upon request. 
  Otherwise entity relations are not hydrated.
  Use `@Autowired EntityManager em;`
- Define the following entities:
```java
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
- Use entityManager to clear the loaded entities, so they will be reloaded in the following test, and thereby re-hydrated.
- Run the test and verify that it succeeds.

#### Solution
```java
@DataJpaTest
public class EducationTests {
    @Autowired EntityManager em;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;

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


    @Test
    public void createAllAssociations() {
        courseRepository.saveAll(Set.of(art, philosophy, math));
        studentRepository.saveAll(Set.of(josh, anna, jane, tom));
        teacherRepository.saveAll(Set.of(johnson, smith, kayne));
        courseRepository.flush();
        em.clear();
    }
}
```

### Exercise 6: Convert test to @BeforeEach
The @Test in previous exercise was just for warming up. To see if we setup the entities right. 
In this exercise you'll convert the test to an initializing method that runs before every test.


- Change the annotation to @BeforeEach
- Change the name to init().

By doing this you now have a solid, well-known data foundation for coming tests. 

#### Solution
```java
@BeforeEach
public void init() {
    courseRepository.saveAll(Set.of(art, philosophy, math));
    studentRepository.saveAll(Set.of(josh, anna, jane, tom));
    teacherRepository.saveAll(Set.of(johnson, smith, kayne));
    courseRepository.flush();
    em.clear();
}
```