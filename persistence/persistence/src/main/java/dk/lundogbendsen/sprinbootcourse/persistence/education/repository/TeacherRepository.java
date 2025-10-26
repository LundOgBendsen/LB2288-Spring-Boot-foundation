package dk.lundogbendsen.sprinbootcourse.persistence.education.repository;

import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long>, TeacherRepositoryCustom {
}
