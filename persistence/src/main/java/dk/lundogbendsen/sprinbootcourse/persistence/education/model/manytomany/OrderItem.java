package dk.lundogbendsen.sprinbootcourse.persistence.education.model.manytomany;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class OrderItem
{
	@ManyToMany(mappedBy = "orderItems")
	Set<Order> orders;
	@Id
	@GeneratedValue
	private Long id;
}
