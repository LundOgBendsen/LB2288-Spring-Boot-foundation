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
public class Soldier {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne //(mappedBy = "soldier")
    Rifle rifle;
}
