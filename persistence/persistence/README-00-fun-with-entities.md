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
  private Set<Course> teaches = new HashSet<>();
}
```
