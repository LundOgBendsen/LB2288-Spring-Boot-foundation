package dk.lundogbendsen.sprinbootcourse.persistence.education.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course extends AuditedEntity {
//    @Id
//    @GeneratedValue
//    private Long id;
    private String subject;
    private Integer points;
    @ManyToOne
    private Teacher teacher;
    @ManyToMany
    private Set<Student> students;
}
