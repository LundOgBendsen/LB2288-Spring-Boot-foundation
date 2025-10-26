# Persistence

## Customizing the Repositories: Custom queries
As can be seen by previous section, the property expressions are quite flexible. But sometimes the names - while quite logical - gets a bit long and unreadable.

In the section we will investigate the custom queries using the @Query annotation. The Language used in @Query is JPQL, which resembles SQL, but with convenience on top to navigate relations.

### Exercise 1: Find By Points, Students and Teacher - and paginate and sort - with @Query
To be able to compare solutions, we rewrite the findCoursesByPointsBetweenAndStudentsAndTeacher query using @Query

The JPQL is very similar to SQL. You can use joins to query on relations. This we will do in this case.

- In Course repository make a method for fetching a list of course by the properties points, students and teacher. Call it something short and intuitive.
- Let is also accept a Pageable in order to paginate and sort the result.
- Make a test that uses the new method in the same way as previous exercise (#4).


#### Solution
```java
    @Query("select c from Course c inner join c.students s where c.points between ?1 and ?2 and s=?3 and c.teacher=?4")
    List<Course> listCourses(Integer start, Integer end, Student student, Teacher teacher, Pageable pageable);
```

```java
    @Test
    public void listCoursesWithPointsBetween_AndStudent_AndTeacher_AndPagination() {
        Student studentAnna = studentRepository.findById(anna.getId()).get();
        Teacher teacherSmith = teacherRepository.getOne(smith.getId());
        final List<Course> coursesByPointsBetween = courseRepository.listCourses(5, 12, studentAnna, teacherSmith, PageRequest.of(0,1, Sort.by("subject").ascending()));
        assertEquals(1, coursesByPointsBetween.size());
    }
```

### Exercise 2: Using attributes of related Entities
The @Query lets you make queries on related Entitites attributes - as opposed to Property Expressions that only lets you query on Relations.

In this exercise we will find students and order them by how many points their courses are worth. Furthermore we add Pageable to support the sort direction thereby letting you find top students or bottom students.

- Make a method on StudentRepository called "findTopStudents" that takes a Pageable as argument.
- Add a @Query to the method that 
  - joins Students with their Courses
  - selects Students and the sum of points of their courses (this sum should be aliased to "points")
  - groups them by student.
- Make a test case that get the topstudents using the new method. 
- Use a Pageable that takes out page 0, page size 10 and sorts by points descending.
- Assert that the Students in the list are ordered by how many points they are "worth".


#### Solution
```java
    @Query("select s, sum(c.points) as points from Student s inner join s.courses c group by s")
    List<Student> findTopStudents(Pageable pageable);
```

```java
    @Test
    public void findTopStudent_ByCoursePoints() {
        List<Student> students = studentRepository.findTopStudents(PageRequest.of(0, 10, Sort.by("points").descending()));
        assertEquals(30, students.get(0).getCourses().stream().mapToInt(Course::getPoints).sum());
        assertEquals(25, students.get(1).getCourses().stream().mapToInt(Course::getPoints).sum());
        assertEquals(20, students.get(2).getCourses().stream().mapToInt(Course::getPoints).sum());
        assertEquals(15, students.get(3).getCourses().stream().mapToInt(Course::getPoints).sum());
    }
```
