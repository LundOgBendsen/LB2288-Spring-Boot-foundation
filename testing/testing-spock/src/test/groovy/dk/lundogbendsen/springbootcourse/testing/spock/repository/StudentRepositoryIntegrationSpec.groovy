package dk.lundogbendsen.springbootcourse.testing.spock.repository

import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Student
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.CourseRepository
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.StudentRepository
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import spock.lang.Title

@Title("Spring Data JPA Integration Specification for StudentRepository with Spock and @DataJpaTest")
@DataJpaTest
@Transactional
class StudentRepositoryIntegrationSpec extends Specification {

    @Autowired
    StudentRepository studentRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    EntityManager entityManager

    def cleanup() {
        // Automatically rolled back due to @DataJpaTest / @Transactional
    }

    def "should persist student and populate JPA auditing timestamps"() {
        given: "a new student entity"
        def student = Student.builder().name("John Doe").build()

        when: "saving the student to the repository and flushing"
        def saved = studentRepository.save(student)
        studentRepository.flush()

        then: "an ID and auditing dates are automatically assigned by Spring Data JPA"
        saved.id != null
        saved.createdDate != null
        saved.updatedDate != null
        saved.name == "John Doe"
    }

    def "should find students with pagination and sorting"() {
        given: "a batch of students saved to database"
        def students = ["David", "Alice", "Charlie", "Bob"].collect { name ->
            studentRepository.save(Student.builder().name(name).build())
        }
        studentRepository.flush()
        entityManager.clear()

        when: "querying for page 0 with size 2 sorted ascending by name"
        def pageable = PageRequest.of(0, 2, Sort.by("name").ascending())
        def page = studentRepository.findAll(pageable)

        then: "the page contains the first 2 alphabetical students"
        page.totalElements == 4
        page.totalPages == 2
        page.content.size() == 2
        page.content[0].name == "Alice"
        page.content[1].name == "Bob"
    }

    def "should delete student by name"() {
        given: "two students in the database"
        def s1 = studentRepository.save(Student.builder().name("TargetStudent").build())
        def s2 = studentRepository.save(Student.builder().name("KeepStudent").build())
        studentRepository.flush()

        when: "deleting by name"
        studentRepository.deleteByName("TargetStudent")
        studentRepository.flush()
        entityManager.clear()

        then: "only the intended student was deleted"
        studentRepository.findById(s1.id).isEmpty()
        studentRepository.findById(s2.id).isPresent()
    }
}
