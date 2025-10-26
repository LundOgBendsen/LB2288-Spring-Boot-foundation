package dk.lundogbendsen.sprinbootcourse.persistence.education.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student extends AuditedEntity{
//    @Id
//    @GeneratedValue
//    private Long id;
    private String name;
    @ManyToMany(mappedBy = "students", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<Course> courses = new HashSet<>();
}
