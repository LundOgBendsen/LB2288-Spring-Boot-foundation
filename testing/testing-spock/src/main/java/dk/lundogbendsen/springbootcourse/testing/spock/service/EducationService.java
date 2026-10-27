package dk.lundogbendsen.springbootcourse.testing.spock.service;

import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Course;
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Student;
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Teacher;
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.CourseRepository;
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.StudentRepository;
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.TeacherRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Transactional
public class EducationService {

    public static final int MAX_COURSES_PER_STUDENT = 5;

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    public EducationService(StudentRepository studentRepository,
                            CourseRepository courseRepository,
                            TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
    }

    public Student createStudent(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be null or blank");
        }
        Student student = Student.builder()
                .name(name.trim())
                .courses(new HashSet<>())
                .build();
        return studentRepository.save(student);
    }

    public Course createCourse(String subject, Integer points) {
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Course subject cannot be null or blank");
        }
        if (points == null || points < 0) {
            throw new IllegalArgumentException("Course points must be non-negative");
        }
        Course course = Course.builder()
                .subject(subject.trim())
                .points(points)
                .students(new HashSet<>())
                .build();
        return courseRepository.save(course);
    }

    public Teacher createTeacher(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Teacher name cannot be null or blank");
        }
        Teacher teacher = Teacher.builder()
                .name(name.trim())
                .teaches(new HashSet<>())
                .build();
        return teacherRepository.save(teacher);
    }

    public Course enrollStudent(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        Set<Course> studentCourses = student.getCourses();
        if (studentCourses == null) {
            studentCourses = new HashSet<>();
            student.setCourses(studentCourses);
        }

        List<Course> enrolled = courseRepository.findByStudents(student);
        if (enrolled != null && enrolled.contains(course) || studentCourses.contains(course)) {
            throw new AlreadyEnrolledException(student.getName(), course.getSubject());
        }

        if (enrolled != null && enrolled.size() >= MAX_COURSES_PER_STUDENT) {
            throw new MaxEnrollmentExceededException(student.getName(), MAX_COURSES_PER_STUDENT);
        }

        Set<Student> courseStudents = course.getStudents();
        if (courseStudents == null) {
            courseStudents = new HashSet<>();
            course.setStudents(courseStudents);
        }

        courseStudents.add(student);
        studentCourses.add(course);

        return courseRepository.save(course);
    }

    public Course removeStudentFromCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        if (course.getStudents() != null) {
            course.getStudents().remove(student);
        }
        if (student.getCourses() != null) {
            student.getCourses().remove(course);
        }

        return courseRepository.save(course);
    }

    public Course assignTeacherToCourse(Long teacherId, Long courseId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException(teacherId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        course.setTeacher(teacher);
        if (teacher.getTeaches() == null) {
            teacher.setTeaches(new HashSet<>());
        }
        teacher.getTeaches().add(course);

        return courseRepository.save(course);
    }

    public int calculateTotalPoints(Student student) {
        if (student == null) {
            return 0;
        }
        List<Course> courses = courseRepository.findByStudents(student);
        if (courses == null || courses.isEmpty()) {
            if (student.getCourses() != null) {
                return student.getCourses().stream()
                        .map(Course::getPoints)
                        .filter(Objects::nonNull)
                        .mapToInt(Integer::intValue)
                        .sum();
            }
            return 0;
        }
        return courses.stream()
                .map(Course::getPoints)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public String determineAcademicStatus(int totalPoints) {
        if (totalPoints >= 30) {
            return "HONORS";
        } else if (totalPoints >= 20) {
            return "PASS_WITH_DISTINCTION";
        } else if (totalPoints >= 10) {
            return "PASS";
        } else {
            return "FAIL";
        }
    }

    @Transactional(readOnly = true)
    public StudentReport getStudentReport(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        List<Course> courses = courseRepository.findByStudents(student);
        List<String> subjectNames = courses.stream()
                .map(Course::getSubject)
                .toList();

        int points = calculateTotalPoints(student);
        String status = determineAcademicStatus(points);

        return new StudentReport(
                student.getId(),
                student.getName(),
                subjectNames,
                points,
                status
        );
    }

    @Transactional(readOnly = true)
    public List<Student> findTopStudents(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        return studentRepository.findTopStudents(PageRequest.of(0, limit));
    }
}
