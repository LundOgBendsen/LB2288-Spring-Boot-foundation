package dk.lundogbendsen.springbootcourse.testing.spock.unit

import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Course
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Student
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Teacher
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.CourseRepository
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.StudentRepository
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.TeacherRepository
import dk.lundogbendsen.springbootcourse.testing.spock.service.*
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

@Title("Unit Specification for EducationService using pure Spock (Mocks, Stubs, Interactions & Exceptions)")
class EducationServiceUnitSpec extends Specification {

    // Spock Mocks for dependencies
    StudentRepository studentRepository = Mock()
    CourseRepository courseRepository = Mock()
    TeacherRepository teacherRepository = Mock()

    // @Subject indicates the class under test
    @Subject
    EducationService educationService

    def setup() {
        // Runs before every feature method (similar to @BeforeEach in JUnit 5)
        educationService = new EducationService(studentRepository, courseRepository, teacherRepository)
    }

    def cleanup() {
        // Runs after every feature method (similar to @AfterEach in JUnit 5)
    }

    def "should successfully create a new student"() {
        given: "a valid student name"
        def studentName = "Alice"
        def savedStudent = Student.builder().name(studentName).courses([] as Set).build()
        savedStudent.id = 1L

        when: "creating the student"
        def result = educationService.createStudent(studentName)

        then: "the repository is called once with matching parameters and returns the saved entity"
        1 * studentRepository.save({ Student s -> s.name == "Alice" }) >> savedStudent
        0 * _ // ensures no other unexpected interactions occur

        and: "the returned student has the expected values"
        result != null
        result.id == 1L
        result.name == "Alice"
    }

    def "should reject creating student with invalid name"() {
        when: "attempting to create a student with blank/null name"
        educationService.createStudent(invalidName)

        then: "an IllegalArgumentException is thrown"
        def ex = thrown(IllegalArgumentException)
        ex.message == "Student name cannot be null or blank"

        and: "the repository is never invoked"
        0 * studentRepository.save(_)

        where: "various invalid names are tested"
        invalidName << [null, "", "   ", "\t\n"]
    }

    def "should successfully enroll student into a course"() {
        given: "an existing student and course"
        def student = Student.builder().name("Bob").courses([] as Set).build()
        student.id = 10L

        def course = Course.builder().subject("Computer Science").points(15).students([] as Set).build()
        course.id = 20L

        // Stubbing repository responses
        studentRepository.findById(10L) >> Optional.of(student)
        courseRepository.findById(20L) >> Optional.of(course)
        courseRepository.findByStudents(student) >> []

        when: "enrolling student into course"
        def updatedCourse = educationService.enrollStudent(10L, 20L)

        then: "courseRepository saves the course containing the enrolled student"
        1 * courseRepository.save({ Course c ->
            c.id == 20L &&
            c.students.contains(student)
        }) >> { Course c -> c }

        and: "the student has the course added to their courses set"
        student.courses.contains(course)
    }

    def "should throw StudentNotFoundException when enrolling non-existent student"() {
        given: "student ID 999 does not exist"
        studentRepository.findById(999L) >> Optional.empty()

        when: "attempting enrollment"
        educationService.enrollStudent(999L, 1L)

        then: "StudentNotFoundException is thrown"
        def ex = thrown(StudentNotFoundException)
        ex.message.contains("999")

        and: "course repository is never touched"
        0 * courseRepository.findById(_)
        0 * courseRepository.save(_)
    }

    def "should throw CourseNotFoundException when course does not exist"() {
        given: "a valid student but missing course"
        def student = Student.builder().name("Charlie").build()
        studentRepository.findById(1L) >> Optional.of(student)
        courseRepository.findById(888L) >> Optional.empty()

        when: "attempting enrollment"
        educationService.enrollStudent(1L, 888L)

        then: "CourseNotFoundException is thrown"
        thrown(CourseNotFoundException)
        0 * courseRepository.save(_)
    }

    def "should throw AlreadyEnrolledException if student is already in the course"() {
        given: "a student already enrolled in Math"
        def student = Student.builder().name("Diana").build()
        def mathCourse = Course.builder().subject("Math").points(10).build()
        mathCourse.id = 5L

        studentRepository.findById(1L) >> Optional.of(student)
        courseRepository.findById(5L) >> Optional.of(mathCourse)
        courseRepository.findByStudents(student) >> [mathCourse]

        when: "enrolling again in the same course"
        educationService.enrollStudent(1L, 5L)

        then: "AlreadyEnrolledException is thrown"
        def ex = thrown(AlreadyEnrolledException)
        ex.message.contains("Diana")
        ex.message.contains("Math")

        0 * courseRepository.save(_)
    }

    def "should throw MaxEnrollmentExceededException when student reaches limit of 5 courses"() {
        given: "a student already enrolled in 5 courses"
        def student = Student.builder().name("Eva").build()
        def existingCourses = (1..5).collect { i -> Course.builder().subject("Course " + i).build() }
        def newCourse = Course.builder().subject("New Course 6").build()
        newCourse.id = 6L

        studentRepository.findById(1L) >> Optional.of(student)
        courseRepository.findById(6L) >> Optional.of(newCourse)
        courseRepository.findByStudents(student) >> existingCourses

        when: "attempting 6th enrollment"
        educationService.enrollStudent(1L, 6L)

        then: "MaxEnrollmentExceededException is thrown"
        thrown(MaxEnrollmentExceededException)
        0 * courseRepository.save(_)
    }

    def "should assign teacher to course"() {
        given: "an existing teacher and course"
        def teacher = Teacher.builder().name("Dr. Smith").teaches([] as Set).build()
        teacher.id = 100L

        def course = Course.builder().subject("Physics").points(10).build()
        course.id = 200L

        teacherRepository.findById(100L) >> Optional.of(teacher)
        courseRepository.findById(200L) >> Optional.of(course)

        when: "assigning teacher"
        educationService.assignTeacherToCourse(100L, 200L)

        then: "course is updated with the teacher and saved"
        1 * courseRepository.save({ Course c ->
            c.teacher == teacher
        }) >> { Course c -> c }

        and: "teacher's teaches set includes the course"
        teacher.teaches.contains(course)
    }

    def "should generate complete student report"() {
        given: "a student with multiple enrolled courses"
        def student = Student.builder().name("Frank").build()
        student.id = 42L

        def course1 = Course.builder().subject("Math").points(15).build()
        def course2 = Course.builder().subject("Physics").points(10).build()
        def course3 = Course.builder().subject("Chemistry").points(10).build()

        studentRepository.findById(42L) >> Optional.of(student)
        courseRepository.findByStudents(student) >> [course1, course2, course3]

        when: "generating student report"
        def report = educationService.getStudentReport(42L)

        then: "report accurately aggregates subjects, points and status"
        report.studentId() == 42L
        report.studentName() == "Frank"
        report.enrolledCourses() == ["Math", "Physics", "Chemistry"]
        report.totalPoints() == 35
        report.academicStatus() == "HONORS"
    }

    def "should calculate total points from student courses correctly"() {
        given: "a student and a list of courses"
        def student = Student.builder().name("Grace").build()
        def courses = pointValues.collect { pts -> Course.builder().subject("Subj").points(pts).build() }

        courseRepository.findByStudents(student) >> courses

        expect: "total points match sum of course points"
        educationService.calculateTotalPoints(student) == expectedSum

        where: "different points combinations are tested"
        pointValues      || expectedSum
        []               || 0
        [5]              || 5
        [5, 10]          || 15
        [5, 10, 15, 20]  || 50
    }
}
