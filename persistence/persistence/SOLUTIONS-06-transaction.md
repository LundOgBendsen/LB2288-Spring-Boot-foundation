# Persistence Transaction and Auditing Solutions

Solutions for the exercises in [README-06-transaction-auditing.md](README-06-transaction-auditing.md).

### Exercise 1: Repository methods are automatically transactional (see SimpleJpaRepository.class)

```java
    @Test
    @Transactional
    public void deleteStudentsByName() {
        final Student christian = Student.builder().name("Christian").build();
        studentRepository.save(christian);
        studentRepository.delete(christian);
    }
```

### Exercise 3: Create a deleteByName method on the StudentRepository

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

```java
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Transactional
    void deleteByName(String name);
}
```

Return to the [exercise guide](README-06-transaction-auditing.md).