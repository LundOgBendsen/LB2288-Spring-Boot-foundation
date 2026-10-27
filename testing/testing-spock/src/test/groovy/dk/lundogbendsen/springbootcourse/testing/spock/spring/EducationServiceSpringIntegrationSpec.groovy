package dk.lundogbendsen.springbootcourse.testing.spock.spring

import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Course
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Student
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Teacher
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.CourseRepository
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.StudentRepository
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.TeacherRepository
import dk.lundogbendsen.springbootcourse.testing.spock.SpockTestingApplication
import dk.lundogbendsen.springbootcourse.testing.spock.service.EducationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import spock.lang.Title

@Title("Spring Boot Full Context Integration Specification with Spock (@SpringBootTest)")
@SpringBootTest(classes = SpockTestingApplication)
@Transactional
class EducationServiceSpringIntegrationSpec extends Specification {

    @Autowired
    EducationService educationService

    @Autowired
    StudentRepository studentRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    TeacherRepository teacherRepository

    def "should perform complete end-to-end education workflow"() {
        when: "creating students, teachers, and courses"
        def student1 = educationService.createStudent("Emma")
        def student2 = educationService.createStudent("Liam")

        def teacher = educationService.createTeacher("Prof. McGonagall")

        def course1 = educationService.createCourse("Transfiguration", 15)
        def course2 = educationService.createCourse("Potions", 15)

        then: "entities are assigned persistent IDs"
        student1.id != null
        student2.id != null
        teacher.id != null
        course1.id != null
        course2.id != null

        when: "assigning teacher to courses and enrolling students"
        educationService.assignTeacherToCourse(teacher.id, course1.id)
        educationService.assignTeacherToCourse(teacher.id, course2.id)

        educationService.enrollStudent(student1.id, course1.id)
        educationService.enrollStudent(student1.id, course2.id)
        educationService.enrollStudent(student2.id, course1.id)

        and: "retrieving student reports"
        def report1 = educationService.getStudentReport(student1.id)
        def report2 = educationService.getStudentReport(student2.id)

        then: "Emma has 30 points and HONORS status"
        report1.studentName() == "Emma"
        report1.totalPoints() == 30
        report1.academicStatus() == "HONORS"
        report1.enrolledCourses() as Set == ["Potions", "Transfiguration"] as Set

        and: "Liam has 15 points and PASS status"
        report2.studentName() == "Liam"
        report2.totalPoints() == 15
        report2.academicStatus() == "PASS"
        report2.enrolledCourses() as Set == ["Transfiguration"] as Set
    }
}
