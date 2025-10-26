package dk.lundogbendsen.sprinbootcourse.persistence.gamer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GamerRepository extends JpaRepository<Gamer, Long> {
}
