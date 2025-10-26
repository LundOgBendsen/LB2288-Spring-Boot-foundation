package dk.lundogbendsen.springbootcourse.testing.example.bookstore.controller;

import dk.lundogbendsen.springbootcourse.testing.example.bookstore.exception.ResourceNotFoundException;
import dk.lundogbendsen.springbootcourse.testing.example.bookstore.model.Book;
import dk.lundogbendsen.springbootcourse.testing.example.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookController {

    private BookRepository bookRepository;

    @Autowired
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @RequestMapping(value = "/books", method = RequestMethod.GET)
    Iterable<Book> findAll() {
        return bookRepository.findAll();
    }

    @RequestMapping(value = "/books", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    void createOperation(@RequestBody Book book) {
        bookRepository.save(book);
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        Book book = findBookOrThrowException(id);
        bookRepository.delete(book);
    }

    private Book findBookOrThrowException(@PathVariable Long id) {
        return bookRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    Book get(@PathVariable Long id) {
        return findBookOrThrowException(id);
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    void editBook(@RequestBody Book book, @PathVariable Long id) {
        findBookOrThrowException(id);
        book.setId(id);
        bookRepository.save(book);

    }


}