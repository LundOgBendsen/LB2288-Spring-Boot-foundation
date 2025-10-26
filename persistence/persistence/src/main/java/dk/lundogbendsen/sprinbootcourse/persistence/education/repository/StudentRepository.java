package dk.lundogbendsen.sprinbootcourse.persistence.education.repository;

import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("select s, sum(c.points) as points from Student s inner join s.courses c group by s")
    List<Student> findTopStudents(Pageable pageable);

//    @Transactional
    void deleteByName(String name);
}
