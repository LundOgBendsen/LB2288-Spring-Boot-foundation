# Persistence Custom Queries Solutions

Solutions for the exercises in [README-04-custom-queries.md](README-04-custom-queries.md).

### Exercise 1: Find By Points, Students and Teacher - and paginate and sort - with @Query

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

Return to the [exercise guide](README-04-custom-queries.md).