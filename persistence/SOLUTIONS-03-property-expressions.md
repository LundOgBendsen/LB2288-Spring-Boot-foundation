# Persistence Property Expressions Solutions

Solutions for the exercises in [README-03-property-expressions.md](README-03-property-expressions.md).

### Exercise 1: find all courses by a Student

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

Return to the [exercise guide](README-03-property-expressions.md).