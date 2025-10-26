package dk.lundogbendsen.sprinbootcourse.persistence.education.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends AuditedEntity{
//    @Id
//    @GeneratedValue
//    private Long id;
    private String name;
    @OneToMany(mappedBy = "teacher")
    private Set<Course> teaches = new HashSet<>();
}
