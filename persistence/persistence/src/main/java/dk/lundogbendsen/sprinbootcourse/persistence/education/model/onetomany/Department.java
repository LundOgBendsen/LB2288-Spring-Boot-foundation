package dk.lundogbendsen.sprinbootcourse.persistence.education.model.onetomany;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Department {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany
    List<Employee> employees;
}
