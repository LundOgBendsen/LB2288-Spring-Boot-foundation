package dk.lundogbendsen.sprinbootcourse.persistence.education.model;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AuditedEntity {
    @Id
    @GeneratedValue
    private Long id;

    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date updatedDate;
}
