# Persistence

## Custom implementations of Repository Methods

Some times it is not enough with @Query or Property Expressions. Or maybe you just want to control everything your self.
Or maybe it is impossible to express the query in a single statement.

For all of these reasons you can take matters in your own hand and implement a custom query which still conforms with
the Repository paradigm.

### Exercise 1: Make a custom Repository implementation getting students by teacher name

Our goal is the extend the existing TeacherRepository with queries that are not easy or convenient to express as
Property Expressions or @Query.
First we need to create a new interface with the new method. Then we make an implementation of that interface. At this
point we have still done nothing for the original TeacherRepository.
But now we modify the TeacherRepository by extending the new interface. Thereby the TeacherRepository have the methods
of the interface.

By convention the Spring looks for an implementation of the interface by looking for a class with the name of the
interface postfixed with the word
"Impl".

- Make a new Interface called `TeacherRepositoryCustom`.
- Add a method `List<Student> getStudents(String teacherName)`.

---

- Make a new class `TeacherRepositoryCustomImpl` - named excatly like the repository with the 'Impl' postfix.
- Implement the `TeacherRepositoryCustom` interface.
- Annotate the class with @Component to enable Autowiring.
- Autowire in the EntityManager to get a Database handle.
- Make a query by calling EntityManager.createQuery () with JPQL:
  `select s from Course c inner join c.students s where c.teacher.name=:name` and Student.class.
- add the teacherName to the query using query.setParameter. The name of the parameter is "name" as can be seen in the
  JPQL.
- Execute the query and return the result.

---

- Modify the `TeacherRepository` and let it extend the new `TeacherRepositoryCustom` interface.

---

- Make a test case that uses the new method for teacher Johnson.
- Assert that the students are josh, jane, and tom.

Solutions for this exercise are in [SOLUTIONS-05-custom-implementation.md](SOLUTIONS-05-custom-implementation.md).