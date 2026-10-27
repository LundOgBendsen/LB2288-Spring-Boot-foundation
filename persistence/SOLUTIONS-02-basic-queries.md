# Persistence Basic Queries Solutions

Solutions for the exercises in [README-02-basic-queries.md](README-02-basic-queries.md).

## Queries

### Exercise 1: Basis Queries - all courses

```java
    @Test
    public void findAllCourses() {
        final List<Course> all = courseRepository.findAll();
        assertEquals(3, all.size());
        assertThat(List.of("art", "philosophy", "math"), containsInAnyOrder(all.get(0).getSubject(), all.get(1).getSubject(), all.get(2).getSubject()));
    }
```

### Exercise 2: Basis Queries - Sort Students, paginate and retrieve last page

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

```java
    @Test
    public void getOneTest() {
        Long id = johnson.getId();
        final Teacher teacher = teacherRepository.findById(id).get();
        assertEquals("Johnson", teacher.getName());
    }
```

### Exercise 4: Basis Queries - get by non-existing ID

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

```java
    @Test
    public void findById_Test() {
        Long id = johnson.getId();
        final Optional<Teacher> teacher = teacherRepository.findById(id);
        assertTrue(teacher.isPresent());
    }
```

### Exercise 6: Basis Queries - Counting

```java
    @Test
    public void countCourses() {
        final long count = courseRepository.count();
        assertEquals(3, count);
    }
```

### Exercise 7: Basis Queries - Navigation

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

### Exercise 8: Basis Queries - Delete Course

```java
    @Test
    public void deleteArtCourse() {
        courseRepository.delete(art);
        courseRepository.flush();
    }
```

### Exercise 9: Basis Queries - Delete Teacher - version 1.

```java
    @Test
    public void deleteTeacherJohnsonV1() {
        teacherRepository.delete(johnson);
        assertThrows(Exception.class, () -> teacherRepository.flush());
    }
```

### Exercise 10: Basis Queries - Delete Teacher - version 2.

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