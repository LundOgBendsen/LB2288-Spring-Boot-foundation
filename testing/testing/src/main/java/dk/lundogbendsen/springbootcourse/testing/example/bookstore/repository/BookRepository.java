package dk.lundogbendsen.springbootcourse.testing.example.bookstore.repository;

import dk.lundogbendsen.springbootcourse.testing.example.bookstore.model.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
    Book findByName(String name);
}
