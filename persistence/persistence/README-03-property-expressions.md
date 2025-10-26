# Persistence

## Customizing the Repositories: Property Expressions
This is as far as we can get using the repositories out of the box. We got at lot of functionality, but they only cover basic stuff.

Repositories support a other options for querying and updating the database.

In this section we will look at Property Expressions.

Property Expressions are methods you add on the Repository whos naming and signature follow a certain convention.


### Exercise 1: find all courses by a Student
We will use a Property Expression query for finding all courses of a given student.

- Open the CourseRepository class.
- Add a new method: `Set<Course> findByStudents(Student student);`
- Make a test that uses the new method for finding the courses of Student Anna.
- Assert that the list of courses consists of philosophy and math.

#### Solution
```java
    List<Course> findByStudents(Student student);
```

```java
    @Test
    public void findCoursesByStudent() {
        final Student student = studentRepository.findById(anna.getId()).get();
        final List<Course> coursesByStudentsContaining = courseRepository.findByStudents(student);
        assertThat(List.of("philosophy", "math"), containsInAnyOrder(coursesByStudentsContaining.get(0).getSubject(), coursesByStudentsContaining.get(1).getSubject()));
    }
```

### Exercise 2: find all courses by a a list of Students
This test is a variation of the previous. But instead of finding courses for 1 student, we will find for a list of students. 
This time we need to ensure that the result only return unique rows by using Distinct.

- Open the CourseRepository class.
- Add a new method: `Set<Course> findDistinctByStudentsIn(Set<Student> students);`
- Make a test that uses the new method for finding the courses of Student Anna and Tom.
- Assert that the list of courses consists of art, philosophy and math.

#### Solution
```java
    List<Course> findDistinctByStudentsIn(Set<Student> students);
```

```java
    @Test
    public void findCoursesByListOfStudents() {
        final Student student1 = studentRepository.findById(anna.getId()).get();
        final Student student2 = studentRepository.findById(tom.getId()).get();
        final List<Course> coursesByStudentsContaining = courseRepository.findDistinctByStudentsIn(List.of(student1, student2));
        assertThat(List.of("art", "philosophy", "math"), containsInAnyOrder(coursesByStudentsContaining.get(0).getSubject(), coursesByStudentsContaining.get(1).getSubject(), coursesByStudentsContaining.get(2).getSubject()));
    }
```

### Exercise 3: Find courses points in a certain range
In the exercise we will make a finder that works on the basic attribute of an entity.

- Add a method to CourseRepository that finds courses by the points of the course if the point lies between a lower and an upper limit (two Integers) 
  (use Intellij to help out here. Start by typing "findAllBy" and let Intellij come to help. You can then continue with "PointsBetween" )
- Make a test case for finding courses by their points.
- Use the bew method to find all courses with points between 5 and 12.
- Assert that you get two courses back.

#### Solution
```java
    List<Course> findCoursesByPointsBetween(Integer start, Integer end);
```

```java
    @Test
    public void findCoursesWithPointsBetween() {
        final List<Course> coursesByPointsBetween = courseRepository.findCoursesByPointsBetween(5, 12);
        assertEquals(2, coursesByPointsBetween.size());
    }
```


### Exercise 4: Find By Points, Students and Teacher - and paginate and sort
In the exercise we will make a method on the courseRepository that work on several properties at once while at the same time paginates and sorts the result.

- Make a new method on courseRepository that is named after Points between, a Student and a Teacher. 
- Also add a Pagable to the method signatures argument list.
- Make a test for finding all courses with points between 5 and 12 that is attended by Anna, and taught by Smith. The result should be paginated with page size 1 and sorted ascending by subject.

**Tip:** 

A Pagination object can be constructed like this:
```java
PageRequest.of(0,1, Sort.by("subject").ascending())
```

#### Solution
```java
    List<Course> findCoursesByPointsBetweenAndStudentsAndTeacher(Integer start, Integer end, Student student, Teacher teacher, Pageable pageable);
```

```java
    @Test
    public void findCoursesWithPointsBetween_AndStudent_AndTeacher_AndPagination() {
        Student studentAnna = studentRepository.findById(anna.getId()).get();
        Teacher teacherSmith = teacherRepository.getOne(smith.getId());
        final List<Course> coursesByPointsBetween = courseRepository.findCoursesByPointsBetweenAndStudentsAndTeacher(5, 12, studentAnna, teacherSmith, PageRequest.of(0,1, Sort.by("subject").ascending()));
        assertEquals(1, coursesByPointsBetween.size());
    }
```