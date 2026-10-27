# Persistence Custom Repository Implementation Solutions

Solutions for the exercises in [README-05-custom-implementation.md](README-05-custom-implementation.md).

### Exercise 1: Make a custom Repository implementation getting students by teacher name

_Make the new interface:_

```java
public interface TeacherRepositoryCustom {
    List<Student> getStudents(String teacherName);
}
```

---
_Implement the new interface - make it a Spring bean in other to use Autowiring._

```java
@Component
public class TeacherRepositoryCustomImpl implements TeacherRepositoryCustom {
    @Autowired
    EntityManager entityManager;

    @Override
    public List<Student> getStudents(String teacherName) {
        final Query query = entityManager.createQuery("select s from Course c inner join c.students s where c.teacher.name=:name", Student.class);
        query.setParameter("name", teacherName);
        return query.getResultList();
    }
}
```

---
_Extend the original interface to inherit the new interface methods. Spring will look for the implementation by naming
convention._

```java
public interface TeacherRepository extends JpaRepository<Teacher, Long>, TeacherRepositoryCustom {
}

```

---
_Use the new methods._

```java
@Test
public void testCustomImplementation() {
    final List<Student> johnson = teacherRepository.getStudents("Johnson");
    assertEquals(3, johnson.size());
}
```

Return to the [exercise guide](README-05-custom-implementation.md).