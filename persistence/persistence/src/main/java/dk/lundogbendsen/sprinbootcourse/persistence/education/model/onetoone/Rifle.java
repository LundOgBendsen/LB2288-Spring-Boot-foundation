package dk.lundogbendsen.sprinbootcourse.persistence.education.model.onetoone;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Rifle {
    @Id
    @GeneratedValue
    private Long id;

//    @MapsId
    @OneToOne //(mappedBy = "rifle")
//        @JoinColumn(unique = true) // Enforce one-to-one
        Soldier soldier;
}
