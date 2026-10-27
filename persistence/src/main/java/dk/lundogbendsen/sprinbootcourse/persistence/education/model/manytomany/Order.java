package dk.lundogbendsen.sprinbootcourse.persistence.education.model.manytomany;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order
{
	@ManyToMany
	Set<OrderItem> orderItems;
	@Id
	@GeneratedValue
	private Long id;
}
