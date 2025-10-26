package dk.lundogbendsen.sprinbootcourse.persistence.education.model.onetomany;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Employee {
    @Id @GeneratedValue
    private Long id;
    @ManyToOne
    Department department;
}
