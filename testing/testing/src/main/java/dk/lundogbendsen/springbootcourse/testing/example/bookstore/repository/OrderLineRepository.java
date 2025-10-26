package dk.lundogbendsen.springbootcourse.testing.example.bookstore.repository;

import dk.lundogbendsen.springbootcourse.testing.example.bookstore.model.OrderLine;
import org.springframework.data.repository.CrudRepository;

public interface OrderLineRepository extends CrudRepository<OrderLine, Long> {
}
