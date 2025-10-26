# Persistence

## Queries

We now have a test class setup with test data we can use for experimenting with queries.

### Exercise 1: Basis Queries - all courses
In this exercise we will familiarize our self with the queries offered by the repositories in their current state.

- Make a test for finding all courses.
- Use the courseRepository findAll() to fetch all courses.
- Assert that you get 3 and that their subjects are art, philosophy, and math

#### Solution
```java
    @Test
    public void findAllCourses() {
        final List<Course> all = courseRepository.findAll();
        assertEquals(3, all.size());
        assertThat(List.of("art", "philosophy", "math"), containsInAnyOrder(all.get(0).getSubject(), all.get(1).getSubject(), all.get(2).getSubject()));
    }
```


### Exercise 2: Basis Queries - Sort Students, paginate and retrieve last page
JpaRepositories support Pagination and Sorting. Some of the built-in queries like findAll takes a Pagable, which encapsulates page page size and sorting.


A Pagable can be built with like this:

`Pageable page = PageRequest.of(3, 1, Sort.sort(Student.class).by(Student::getName).ascending());`

- Make a test for fetching last page of Students with page size 1, sorted by student name ascending.
- Assert that you the students name is Tom.

#### Solution
```java
    @Test
    public void findAllStudentsPaginatedAndSorted() {
        Pageable page = PageRequest.of(3, 1, Sort.sort(Student.class).by(Student::getName).ascending());
        Page<Student> all = studentRepository.findAll(page);

        assertEquals(4, all.getTotalElements());
        assertEquals(4, all.getTotalPages());
        assertEquals("Tom", all.getContent().get(0).getName());
    }
```

### Exercise 3: Basis Queries - get by ID
You can get an Entity directly if you know the ID.

- Make a test for fetching Teacher Johnson. Use the findById().
- Assert that the fetched teachers name is Johnson.

#### Solution
```java
    @Test
    public void getOneTest() {
        Long id = johnson.getId();
        final Teacher teacher = teacherRepository.findById(id).get();
        assertEquals("Johnson", teacher.getName());
    }
```

### Exercise 4: Basis Queries - get by non-existing ID
@Deprecated as we no longer use getOne()...

What happens if you getOne with a non-existing ID? You get a handle that proxies a null value.

- Make a test for fetching Teacher Johnson. Use the getOne(100L).
- Assert that the returned value is not null (assertNotNull()).
- try to get the set of courses from the teacher. (catch EntityNotFoundException)
- Fail() if that succeed. 

#### Solution
```java
    @Test
    public void getOne_NonExistingId_Test() {
        Long id = 100L;
        Teacher teacher = teacherRepository.getOne(id);
        assertNotNull(teacher);
        assertThrows(EntityNotFoundException.class, teacher::getTeaches);
    }
```

### Exercise 5: Basis Queries - findById
There is another way to get an entity by ID. Use the findById(). It returns an Optional which you can use to ask if there is an entity or not.

Difference to getOne() is that findById is EAGER, whereas the getOne is LAZY. EAGER means that an actual database query is performed immediately, while the LAZY will do it when a property is accessed the first time. It will then throw an EntityNotFoundException if it does not exists.

- Make a test for fetching Teacher Johnson. Use the getOne(johnson.findById()).
- Assert that the returned value is present using the Optional.isPresent()

#### Solution
```java
    @Test
    public void findById_Test() {
        Long id = johnson.getId();
        final Optional<Teacher> teacher = teacherRepository.findById(id);
        assertTrue(teacher.isPresent());
    }
```

### Exercise 6: Basis Queries - Counting
You can count entities using count() method.

- Make a test for counting courses.
- Use the courseRepository.count() to fetch the count.
- Assert that there are 3 courses.

#### Solution
```java
    @Test
    public void countCourses() {
        final long count = courseRepository.count();
        assertEquals(3, count);
    }
```

### Exercise 7: Basis Queries - Navigation
In this exercise we will see how navigation works. We load a Student, find a Course, and from that the Teacher.

- Make a test case for navigation.
- Load the Student Tom.
- Get the art Course from Toms courses (Tip: use stream().filter().findFirst())
- Get the Teacher from that Course.
- Assert that the Teachers name is Johnson.

#### Solution
```java
    @Test
    public void navigateFromStudentToTeacher() {
        final Student student = studentRepository.findById(tom.getId()).get();
        final Course course = student.getCourses().stream().filter(c -> c.getSubject().equals("art")).findFirst().get();
        final Teacher teacher = course.getTeacher();
        assertEquals("Johnson", teacher.getName());
    }
```



## Modifying operations
Reading is one thing - updating another. 
There are some things to consider when updating entitites especially if they are related to other entities.

The things to consider are this: foreign key constraints.

When deleting entities, we must ensure that all references are deleted as well.

When updating relations, we must update any uni-directional relation separately.

Furthermore, when Hibernate loads an Entity from the database, it also fetches its relations. So loading a Course, the Teacher and Students are also loaded. 
The Course is said to be hydrated. But collections are loaded into an immutable Collection making it impossible to add or remove relations.

The trick is to make a new collection, copy over any relation to remain, and replace the original. We'll see a few examples of this.


### Exercise 8: Basis Queries - Delete Course
We'll try to delete a course in this exercise. 

- Make a test for deleting the Art course.
- use courseRepository.delete() to delete it.
- use courseRepository.flush() to force Hibernate to execute the queries in the transaction.
 
Even tough there is a Teacher and some Students associated with the Course, the delete succeeds because the relations are owned by the Course, and therefore are handled when the Course is deleted.

#### Solution
```java
    @Test
    public void deleteArtCourse() {
        courseRepository.delete(art);
        courseRepository.flush();
    }
```

### Exercise 9: Basis Queries - Delete Teacher - version 1.
We'll try to delete a Teacher in this exercise. 

- Make a test for deleting the the Teacher Johnson.
  
- use teacherRepository.delete() to delete it.
- use teacherRepository.flush() to force Hibernate to execute the queries in the transaction.
 
This will fail because of referential integrity (the relation to a Course is not owned by the Teacher).

#### Solution
```java
    @Test
    public void deleteTeacherJohnsonV1() {
        teacherRepository.delete(johnson);
        assertThrows(Exception.class, () -> teacherRepository.flush());
    }
```

### Exercise 10: Basis Queries - Delete Teacher - version 2.
We'll try to delete a Teacher in this exercise. But this time we will remove associations first.

- Make a test for deleting the the Teacher Johnson.
- Load the Teacher Johnson.
- Update his courses by setting their teacher to null.
- use teacherRepository.delete() to delete it.
- use teacherRepository.flush() to force Hibernate to execute the queries in the transaction.
 
#### Solution
```java
    @Test
    public void deleteTeacherJohnson() {
        Teacher teacherJohnson = teacherRepository.findById(johnson.getId()).get();
        teacherJohnson.getTeaches().forEach(course -> course.setTeacher(null));
        teacherRepository.delete(teacherJohnson);
        teacherRepository.flush();
    }
```

### Exercise 11: Basis Queries - Delete Student
Students are in a ManyToMany relationship with Courses. We'll try delete one.

- Make a test for deleting a Student.
- Load the Student Anna.
- Iterate over her Courses and remove her from them.
- Delete the Student.

#### Solution
```java
    public void deleteStudent_update_courses() {
        final Student student = studentRepository.findById(anna.getId()).get();

        // We must update the Course side of the relation in order to delete a student
        student.getCourses().forEach(course -> {    
            course.getStudents().remove(student);
        });
        studentRepository.delete(student);
        studentRepository.flush();
    }
```

### Exercise 12: Add Student and assign to Course
In this exercise we will add a new Student and put into a Course.

- Make a new test for Creating a Student (Jim).
- Load the Art course and add the Student.
- Load the Math course and add the Student.
- Save the courses and the student.
- Flush the course repository to force database write.
- Load the student using the studentRepository.
- Assert that the student have 2 courses in his courses collection.

#### Solution
```java
@Test
public void createStudent_addToCourses() {
    final Student jim = Student.builder().name("Jim").build();
    studentRepository.save(jim);
    final Course courseArt = courseRepository.findById(art.getId()).get();
    courseArt.getStudents().add(jim);
    final Course courseMath = courseRepository.findById(math.getId()).get();
    courseMath.getStudents().add(jim);
    courseRepository.save(courseArt);
    courseRepository.save(courseMath);
    courseRepository.flush();

    final Student student = studentRepository.findById(jim.getId()).get();
    // Tell entity manager to refresh the student because the cached version is not updated with the courses set above.
    em.refresh(student);
    assertEquals(2, student.getCourses().size());
}
```

### Exercise 13: Remove Student from Course
In this exercise we will remove Jane from the Course Philosophy.

- Make a test case for removing a Student from a Course.
- Load the Course "Philosophy".
- Load the Student Jane, and assert that she have 2 courses.
- Remove Jane from the set of Students.
- Save the Course.
- Flush.
- Use EntityManager to refresh the Student (force re-hydratation).
- Assert that the student now only have 1 course in her list.

#### Solution
```java
    @Test
    public void removeStudentFromCourse() {
        final Course course = courseRepository.findById(philosophy.getId()).get();
        Student student = studentRepository.findById(jane.getId()).get();
        assertEquals(2, student.getCourses().size());
        course.getStudents().remove(student);
        courseRepository.save(course);
        courseRepository.flush();
        em.refresh(student);
        assertEquals(1, student.getCourses().size());
    }
```
