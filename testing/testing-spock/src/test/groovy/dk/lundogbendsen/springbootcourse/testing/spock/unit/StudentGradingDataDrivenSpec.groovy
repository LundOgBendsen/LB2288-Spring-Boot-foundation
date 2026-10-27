package dk.lundogbendsen.springbootcourse.testing.spock.unit

import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Course
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Student
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.CourseRepository
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.StudentRepository
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.TeacherRepository
import dk.lundogbendsen.springbootcourse.testing.spock.service.EducationService
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

@Title("Data-Driven Testing Specification in Spock (@Unroll, Data Tables, Data Pipes, Power Assertions)")
class StudentGradingDataDrivenSpec extends Specification {

    // @Shared fields are initialized once per Specification class
    @Shared
    EducationService educationService

    def setupSpec() {
        // Runs once before all feature methods in this Specification
        def studentRepo = Mock(StudentRepository)
        def courseRepo = Mock(CourseRepository)
        def teacherRepo = Mock(TeacherRepository)
        educationService = new EducationService(studentRepo, courseRepo, teacherRepo)
    }

    @Unroll
    def "Total points #totalPoints maps to academic status '#expectedStatus'"() {
        expect: "the status matches the grading criteria"
        educationService.determineAcademicStatus(totalPoints) == expectedStatus

        where: "data table defines points and expected statuses"
        totalPoints || expectedStatus
        0           || "FAIL"
        5           || "FAIL"
        9           || "FAIL"
        10          || "PASS"
        15          || "PASS"
        19          || "PASS"
        20          || "PASS_WITH_DISTINCTION"
        25          || "PASS_WITH_DISTINCTION"
        29          || "PASS_WITH_DISTINCTION"
        30          || "HONORS"
        45          || "HONORS"
        100         || "HONORS"
    }

    @Unroll
    def "Student with courses #coursePointsList accumulates #expectedPoints points"() {
        given: "a student with direct set of courses"
        Set<Course> courses = coursePointsList.collect { pts ->
            Course.builder().subject("Subj-" + pts).points(pts).build()
        } as Set
        def student = Student.builder().name("TestStudent").courses(courses).build()

        expect: "calculation correctly sums points from entity course set"
        educationService.calculateTotalPoints(student) == expectedPoints

        where: "data table with inputs, outputs, and descriptive columns"
        coursePointsList     || expectedPoints
        []                   || 0
        [10]                 || 10
        [5, 5, 5]            || 15
        [10, 20, 30]         || 60
        [0, 5, 10, 15]       || 30
    }

    @Unroll
    def "Course creation succeeds for valid subject='#subject' and points=#points"() {
        given: "a mocked course repository returning saved entity"
        def courseRepo = Mock(CourseRepository)
        def service = new EducationService(Mock(StudentRepository), courseRepo, Mock(TeacherRepository))

        when: "creating course"
        def course = service.createCourse(subject, points)

        then: "repository saves the course"
        1 * courseRepo.save({ Course c -> c.subject == subject && c.points == points }) >> { Course c -> c }
        course != null
        course.subject == subject
        course.points == points

        where: "valid course data combinations"
        subject    | points
        "Math"     | 10
        "Physics"  | 0
        "English"  | 5
    }

    @Unroll
    def "Course creation rejected for invalid subject='#subject' and points=#points"() {
        given: "education service instance"
        def service = new EducationService(Mock(StudentRepository), Mock(CourseRepository), Mock(TeacherRepository))

        when: "attempting to create invalid course"
        service.createCourse(subject, points)

        then: "IllegalArgumentException is thrown"
        thrown(IllegalArgumentException)

        where: "invalid boundary combinations"
        subject      | points
        ""           | 10
        "   "        | 5
        null         | 5
        "Chemistry"  | -1
        "Biology"    | null
    }

    def "Data pipe syntax demonstration: all positive numbers #number are greater than zero"() {
        expect:
        number > 0

        where: "data pipe syntax using << operator"
        number << [1, 2, 5, 10, 100, 999]
    }

    def "Multiple data pipes combined into Cartesian-like test matrix"() {
        expect:
        (a + b) == expectedSum

        where:
        a << [1, 2, 3]
        b << [10, 20, 30]
        expectedSum = a + b
    }
}
