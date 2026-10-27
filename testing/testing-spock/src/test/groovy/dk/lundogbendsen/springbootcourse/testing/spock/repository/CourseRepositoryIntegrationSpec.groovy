package dk.lundogbendsen.springbootcourse.testing.spock.repository

import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Course
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Student
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Teacher
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.CourseRepository
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.StudentRepository
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.TeacherRepository
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import spock.lang.Title

@Title("Spring Data JPA Integration Specification for CourseRepository with Spock")
@DataJpaTest
@Transactional
class CourseRepositoryIntegrationSpec extends Specification {

    @Autowired
    CourseRepository courseRepository

    @Autowired
    StudentRepository studentRepository

    @Autowired
    TeacherRepository teacherRepository

    @Autowired
    EntityManager entityManager

    // Test fixtures
    Student josh
    Student jane
    Student tom
    Teacher johnson
    Teacher smith
    Course art
    Course math
    Course philosophy

    def setup() {
        josh = studentRepository.save(Student.builder().name("Josh").build())
        jane = studentRepository.save(Student.builder().name("Jane").build())
        tom = studentRepository.save(Student.builder().name("Tom").build())

        johnson = teacherRepository.save(Teacher.builder().name("Johnson").build())
        smith = teacherRepository.save(Teacher.builder().name("Smith").build())

        art = courseRepository.save(Course.builder()
                .subject("Art")
                .points(5)
                .teacher(johnson)
                .students([josh, jane] as Set)
                .build())

        math = courseRepository.save(Course.builder()
                .subject("Math")
                .points(15)
                .teacher(johnson)
                .students([josh, tom] as Set)
                .build())

        philosophy = courseRepository.save(Course.builder()
                .subject("Philosophy")
                .points(10)
                .teacher(smith)
                .students([jane, tom] as Set)
                .build())

        courseRepository.flush()
        entityManager.clear()
    }

    def "should find courses by student"() {
        when: "finding courses enrolled by Josh"
        def joshCourses = courseRepository.findByStudents(josh)

        then: "Josh is enrolled in Art and Math"
        joshCourses.size() == 2
        joshCourses*.subject as Set == ["Art", "Math"] as Set
    }

    def "should find distinct courses by students in list"() {
        when: "finding distinct courses for Josh and Tom"
        def courses = courseRepository.findDistinctByStudentsIn([josh, tom])

        then: "all three courses are returned without duplicates"
        courses.size() == 3
        courses*.subject as Set == ["Art", "Math", "Philosophy"] as Set
    }

    def "should find course IDs by student names"() {
        when: "querying course IDs for student name 'Josh'"
        def courseIds = courseRepository.findCourseIdsByStudentNames(["Josh"])

        then: "returns IDs for Art and Math"
        courseIds.size() == 2
        courseIds.contains(art.id)
        courseIds.contains(math.id)
    }

    def "should find courses by student names"() {
        when: "querying courses for student names 'Jane' and 'Tom'"
        def courses = courseRepository.findCoursesByStudentNames(["Jane", "Tom"])

        then: "returns all courses where either Jane or Tom are enrolled"
        courses.size() == 3
        courses*.subject as Set == ["Art", "Math", "Philosophy"] as Set
    }

    def "should find top students with course count projection"() {
        when: "finding top students aggregation"
        def topStudents = courseRepository.findTopStudents()

        then: "each student has accurate course count"
        topStudents != null
        topStudents.size() == 3
        // Josh, Jane, Tom each have 2 courses
        topStudents.every { it.counting == 2L }
        topStudents*.name as Set == ["Josh", "Jane", "Tom"] as Set
    }

    def "should find courses by points between range"() {
        when: "querying points between 8 and 16"
        def courses = courseRepository.findCoursesByPointsBetween(8, 16)

        then: "Philosophy (10) and Math (15) are matched"
        courses.size() == 2
        courses*.subject as Set == ["Philosophy", "Math"] as Set
    }

    def "should list courses with complex criteria (points, student, teacher, pageable)"() {
        when: "querying courses for teacher Johnson, student Josh, points between 1 and 20"
        def pageable = PageRequest.of(0, 10)
        def courses = courseRepository.listCourses(1, 20, josh, johnson, pageable)

        then: "Art and Math match the criteria"
        courses.size() == 2
        courses*.subject as Set == ["Art", "Math"] as Set
    }
}
