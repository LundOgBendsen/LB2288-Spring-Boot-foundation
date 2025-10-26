package dk.lundogbendsen.sprinbootcourse.persistence.education.repository;

import dk.lundogbendsen.sprinbootcourse.persistence.education.model.QBExample;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QBExampleRepository extends JpaRepository<QBExample, Long> {
}
