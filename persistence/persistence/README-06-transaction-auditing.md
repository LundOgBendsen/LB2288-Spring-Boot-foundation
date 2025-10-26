# Persistence

## Transactions
All database operations MUST be performed in a Transaction. 
The reason we have not been bothered by this so far, is because all Spring sets up an implicit Transaction around all the test cases.

To test how transactions works, we must disable automatic transactions and take control our selves. We can do this by adding the @Transactional annotation on our test case. And then we can tweak that annotation to allow testing it.

### Exercise 1: Repository methods are automatically transactional (see SimpleJpaRepository.class)
In this exercise we are going to realize that repository methods originating from the CrudRepository are marked with transactional.

- Make a test case for deleting a student.
- Create a new student and use studentRepository to save it.
- Immediately after - use studentRepository to delete it again.
- Run the test

Of course this works. What is happening is that Spring is implicitly setting up a transaction around the test case. Let's make that explicit:

- Annotate the test case with @Transactional.
- Run the test again.

It still works. Just this time you are explicitly controlling the transaction.

#### Solution
```java
    @Test
    @Transactional
    public void deleteStudentsByName() {
        final Student christian = Student.builder().name("Christian").build();
        studentRepository.save(christian);
        studentRepository.delete(christian);
    }
```

### Exercise 2: Disable propagation of the explicit transaction
In this exercise we continue working on the test from before. This time we'll disable transaction propagation meaning that the repository will not be part of the overall transaction.

- Change the Transactional annotation to `@Transactional(propagation = Propagation.NEVER)`
- Run the test again.

This STILL works. That is because the delete() operation comes from CrudRepository, and therefore is transactional.

```java
    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void deleteStudentsByName() {
        final Student christian = Student.builder().name("Christian").build();
        studentRepository.save(christian);
        studentRepository.delete(christian);
    }
```

### Exercise 3: Create a deleteByName method on the StudentRepository
In this exercise we will create a new way of deleting a Student by name, and see how that works with transactions.

- Make a method `void deleteByName(String name);` on the StudentRepository.
- Update the testcase from before to delete the student using the new method.
- Run the test case. Will it work?

NO! It fails because there is no transaction associated with the call to the repository, and we did not mark it ourselves ("No EntityManager with actual transaction available for current thread"). 
There MUST be a transaction.

The methods YOU provide on a Repository does not automatically have a transaction associated.

#### Solution
```java
public interface StudentRepository extends JpaRepository<Student, Long> {
    void deleteByName(String name);
}
```
---
```java
    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void deleteStudentsByName() {
        final Student christian = Student.builder().name("Christian").build();
        studentRepository.save(christian);
//        studentRepository.delete(christian);
        studentRepository.deleteByName("Christian");
    }
```

### Exercise 4: Include deleteByName in a Transaction
In this exercise we are going to execute the deleteByName in a transaction. There are two approaces to this.

Either we are marking the repository method explicitly with the @Transactional annotation, OR we are propagating the transaction from the testmethod.

- Update the repository method by annotating it with `@Transactional`
- Run the test. Works it?

YES! Because it runs in an explicit transaction of its own.

#### Solution
```java
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Transactional
    void deleteByName(String name);
}
```


## Auditing
You can extend your Entity model with fields for capturing Who changed/updated a row When.

In this section we are going to explore how this works. We are only working with the When part because the Who part require Spring Security.

### Exercise 5: Add Auditing to the Education Entity model Course
In this exercise we are going to add timestamps for created and updated.

- On Course Entity model add the following fields:
  - `@CreatedDate private Date createdAt;` 
  - `@LastModifiedDate private Date updatedAt;`
- Annotate the class with `@EntityListeners(AuditingEntityListener.class)`.
- Finally enable Auditing on application level by adding to the main-class `@EnableJpaAuditing`.

- Make a test case for creating and updating a Course.
- Make a new Course where you set only the points and subject. No Student or Teacher.
- Save it and flush it.
- extract the fields createdAt and updatedAt into two variables.
- Assert that they are not null.

- Update the points to another value.
- Save and flush.
- assert that the createdAt hold the same value as the variable from before.
- assert that the updatedAt is after the value of the variable from before.

#### Solution
```java
    @Test
    public void createAndUpdateCourse_WatchAuditing() {
        // Create
        Course it = Course.builder().subject("it").points(20).build();
        courseRepository.save(it);
        courseRepository.flush();
        final Date createdDate = it.getCreatedDate();
        final Date updatedDate = it.getUpdatedDate();
        assertNotNull(createdDate);
        assertNotNull(updatedDate);

        // Update
        it.setPoints(10);
        courseRepository.save(it);
        courseRepository.flush();
        assertEquals(createdDate, it.getCreatedDate());
        assertTrue(it.getUpdatedDate().after(updatedDate));
    }
```

### Exercise 6 - Using a Mapped super class to hold common fields
In this exercise we are going to extract common fields from the Entity model into a class, we will annotate as a `@MappedSuperClass`.

Common fields are:
- id
- createdAt
- updatedAt

**Prepare:**
- Create this class:
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AuditedEntity {
   @Id
   @GeneratedValue
   private Long id;

   @CreatedDate
   private Date created;
   @LastModifiedDate
   private Date lastModified;
}
```
- Update the Course, Student and Teacher entitites and remove id, createdAt and updatedAt fields.
- Let them extend the new class.
- Run all test test cases to make sure everything still works the same.

#### Solution
```java
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course extends AuditedEntity{
    private String subject;
    private Integer points;
    @ManyToOne
    private Teacher teacher;
    @ManyToMany
    private Set<Student> students = new HashSet<>();
}
```
---
```java
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends AuditedEntity{
    private String name;
    @OneToMany(mappedBy = "teacher")
    private Set<Course> teaches = new HashSet<>();
}
```
---
```java
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student extends AuditedEntity{
    private String name;
    @ManyToMany(mappedBy = "students")
    private Set<Course> courses = new HashSet<>();
}
```
