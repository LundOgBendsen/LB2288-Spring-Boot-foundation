# Persistence

## Customizing the Repositories: Custom queries

As can be seen by previous section, the property expressions are quite flexible. But sometimes the names - while quite
logical - gets a bit long and unreadable.

In the section we will investigate the custom queries using the @Query annotation. The Language used in @Query is JPQL,
which resembles SQL, but with convenience on top to navigate relations.

### Exercise 1: Find By Points, Students and Teacher - and paginate and sort - with @Query

To be able to compare solutions, we rewrite the findCoursesByPointsBetweenAndStudentsAndTeacher query using @Query

The JPQL is very similar to SQL. You can use joins to query on relations. This we will do in this case.

- In Course repository make a method for fetching a list of course by the properties points, students and teacher. Call
  it something short and intuitive.
- Let is also accept a Pageable in order to paginate and sort the result.
- Make a test that uses the new method in the same way as previous exercise (#4).

### Exercise 2: Using attributes of related Entities

The @Query lets you make queries on related Entitites attributes - as opposed to Property Expressions that only lets you
query on Relations.

In this exercise we will find students and order them by how many points their courses are worth. Furthermore we add
Pageable to support the sort direction thereby letting you find top students or bottom students.

- Make a method on StudentRepository called "findTopStudents" that takes a Pageable as argument.
- Add a @Query to the method that
    - joins Students with their Courses
    - selects Students and the sum of points of their courses (this sum should be aliased to "points")
    - groups them by student.
- Make a test case that get the topstudents using the new method.
- Use a Pageable that takes out page 0, page size 10 and sorts by points descending.
- Assert that the Students in the list are ordered by how many points they are "worth".

Solutions for these exercises are in [SOLUTIONS-04-custom-queries.md](SOLUTIONS-04-custom-queries.md).
