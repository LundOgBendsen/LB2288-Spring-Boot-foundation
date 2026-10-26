# Persistence

## Queries

We now have a test class setup with test data we can use for experimenting with queries.

### Exercise 1: Basis Queries - all courses

In this exercise we will familiarize our self with the queries offered by the repositories in their current state.

- Make a test for finding all courses.
- Use the courseRepository findAll () to fetch all courses.
- Assert that you get 3 and that their subjects are art, philosophy, and math

### Exercise 2: Basis Queries - Sort Students, paginate and retrieve last page

JpaRepositories support Pagination and Sorting. Some of the built-in queries like findAll takes a Pagable, which
encapsulates page page size and sorting.

A Pagable can be built with like this:

`Pageable page = PageRequest.of(3, 1, Sort.sort(Student.class).by(Student::getName).ascending());`

- Make a test for fetching last page of Students with page size 1, sorted by student name ascending.
- Assert that you the students name is Tom.

### Exercise 3: Basis Queries - get by ID

You can get an Entity directly if you know the ID.

- Make a test for fetching Teacher Johnson. Use the findById ().
- Assert that the fetched teachers name is Johnson.

### Exercise 4: Basis Queries - get by non-existing ID

@Deprecated as we no longer use getOne ()...

What happens if you getOne with a non-existing ID? You get a handle that proxies a null value.

- Make a test for fetching Teacher Johnson. Use the getOne (100L).
- Assert that the returned value is not null (assertNotNull ()).
- try to get the set of courses from the teacher. (catch EntityNotFoundException)
- Fail () if that succeed.

### Exercise 5: Basis Queries - findById

There is another way to get an entity by ID. Use the findById (). It returns an Optional which you can use to ask if
there is an entity or not.

Difference to getOne () is that findById is EAGER, whereas the getOne is LAZY. EAGER means that an actual database query
is performed immediately, while the LAZY will do it when a property is accessed the first time. It will then throw an
EntityNotFoundException if it does not exists.

- Make a test for fetching Teacher Johnson. Use the getOne (johnson.findById ()).
- Assert that the returned value is present using the Optional.isPresent ()

### Exercise 6: Basis Queries - Counting

You can count entities using count () method.

- Make a test for counting courses.
- Use the courseRepository.count () to fetch the count.
- Assert that there are 3 courses.

### Exercise 7: Basis Queries - Navigation

In this exercise we will see how navigation works. We load a Student, find a Course, and from that the Teacher.

- Make a test case for navigation.
- Load the Student Tom.
- Get the art Course from Toms courses (Tip: use stream ().filter ().findFirst ())
- Get the Teacher from that Course.
- Assert that the Teachers name is Johnson.

## Modifying operations

Reading is one thing - updating another.
There are some things to consider when updating entitites especially if they are related to other entities.

The things to consider are this: foreign key constraints.

When deleting entities, we must ensure that all references are deleted as well.

When updating relations, we must update any uni-directional relation separately.

Furthermore, when Hibernate loads an Entity from the database, it also fetches its relations. So loading a Course, the
Teacher and Students are also loaded.
The Course is said to be hydrated. But collections are loaded into an immutable Collection making it impossible to add
or remove relations.

The trick is to make a new collection, copy over any relation to remain, and replace the original. We'll see a few
examples of this.

### Exercise 8: Basis Queries - Delete Course

We'll try to delete a course in this exercise.

- Make a test for deleting the Art course.
- use courseRepository.delete () to delete it.
- use courseRepository.flush () to force Hibernate to execute the queries in the transaction.

Even tough there is a Teacher and some Students associated with the Course, the delete succeeds because the relations
are owned by the Course, and therefore are handled when the Course is deleted.

### Exercise 9: Basis Queries - Delete Teacher - version 1.

We'll try to delete a Teacher in this exercise.

- Make a test for deleting the the Teacher Johnson.

- use teacherRepository.delete () to delete it.
- use teacherRepository.flush () to force Hibernate to execute the queries in the transaction.

This will fail because of referential integrity (the relation to a Course is not owned by the Teacher).

### Exercise 10: Basis Queries - Delete Teacher - version 2.

We'll try to delete a Teacher in this exercise. But this time we will remove associations first.

- Make a test for deleting the the Teacher Johnson.
- Load the Teacher Johnson.
- Update his courses by setting their teacher to null.
- use teacherRepository.delete () to delete it.
- use teacherRepository.flush () to force Hibernate to execute the queries in the transaction.

### Exercise 11: Basis Queries - Delete Student

Students are in a ManyToMany relationship with Courses. We'll try delete one.

- Make a test for deleting a Student.
- Load the Student Anna.
- Iterate over her Courses and remove her from them.
- Delete the Student.

### Exercise 12: Add Student and assign to Course

In this exercise we will add a new Student and put into a Course.

- Make a new test for Creating a Student (Jim).
- Load the Art course and add the Student.
- Load the Math course and add the Student.
- Save the courses and the student.
- Flush the course repository to force database write.
- Load the student using the studentRepository.
- Assert that the student have 2 courses in his courses collection.

### Exercise 13: Remove Student from Course

In this exercise we will remove Jane from the Course Philosophy.

- Make a test case for removing a Student from a Course.
- Load the Course "Philosophy".
- Load the Student Jane, and assert that she have 2 courses.
- Remove Jane from the set of Students.
- Save the Course.
- Flush.
- Use EntityManager to refresh the Student (force re-hydratation).
- Assert that the student now has only 1 course in her list.


Solutions for these exercises are in [SOLUTIONS-02-basic-queries.md](SOLUTIONS-02-basic-queries.md).

