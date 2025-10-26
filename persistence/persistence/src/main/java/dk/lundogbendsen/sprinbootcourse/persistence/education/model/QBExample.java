package dk.lundogbendsen.sprinbootcourse.persistence.education.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class QBExample {
    @Id
    @GeneratedValue
    private Long id;
    private String p1;
    private Long p2;
    @ElementCollection
    private List<String> p3;
}
