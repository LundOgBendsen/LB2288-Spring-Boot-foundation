package dk.lundogbendsen.springbootcourse.testing.example.bookstore.repository;

import dk.lundogbendsen.springbootcourse.testing.example.bookstore.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
