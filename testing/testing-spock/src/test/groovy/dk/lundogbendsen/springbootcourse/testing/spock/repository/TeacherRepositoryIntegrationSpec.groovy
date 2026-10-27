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
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import spock.lang.Title

@Title("Spring Data JPA Custom Repository Specification for TeacherRepository with Spock")
@DataJpaTest
@Transactional
class TeacherRepositoryIntegrationSpec extends Specification {

    @Autowired
    TeacherRepository teacherRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    StudentRepository studentRepository

    @Autowired
    EntityManager entityManager

    Teacher profKayne
    Student anna
    Student josh
    Course math

    def setup() {
        profKayne = teacherRepository.save(Teacher.builder().name("Kayne").build())
        anna = studentRepository.save(Student.builder().name("Anna").build())
        josh = studentRepository.save(Student.builder().name("Josh").build())

        math = courseRepository.save(Course.builder()
                .subject("Advanced Math")
                .points(15)
                .teacher(profKayne)
                .students([anna, josh] as Set)
                .build())

        courseRepository.flush()
        entityManager.clear()
    }

    def "should execute custom repository method getStudents to find all students taught by teacher"() {
        when: "calling custom repository method getStudents('Kayne')"
        def students = teacherRepository.getStudents("Kayne")

        then: "both Anna and Josh are returned"
        students != null
        students.size() == 2
        students*.name as Set == ["Anna", "Josh"] as Set
    }

    def "should return empty list when teacher teaches no students"() {
        given: "a teacher with no courses"
        teacherRepository.save(Teacher.builder().name("NewTeacher").build())
        teacherRepository.flush()

        when: "querying students for the new teacher"
        def students = teacherRepository.getStudents("NewTeacher")

        then: "result is empty"
        students.isEmpty()
    }
}
