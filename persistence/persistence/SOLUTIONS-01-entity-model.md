# Persistence Entity Model Solutions

Solutions for the exercises in [README-01-entity-model.md](README-01-entity-model.md).

## Exercise 1: Create a Domain model for the Education case

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

## Exercise 2: Upgrade the Domain model to an Entity model

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

## Exercise 3: Make repositories for the entity model

```java
public interface CourseRepository extends JpaRepository<Course, Long> {}
```

```java
public interface StudentRepository extends JpaRepository<Student, Long> {}
```

```java
public interface TeacherRepository extends JpaRepository<Teacher, Long> {}
```

## Exercise 4: Run the application and see the DDL generated

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

## Exercise 5: Make unit tests (@DataJpaTest) to try out the repositories

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

## Exercise 6: Convert test to @BeforeEach

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