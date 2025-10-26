package dk.lundogbendsen.springbootcourse.testing.example.bookstore.service;

import dk.lundogbendsen.springbootcourse.testing.example.bookstore.model.Order;
import dk.lundogbendsen.springbootcourse.testing.example.bookstore.model.OrderLine;
import dk.lundogbendsen.springbootcourse.testing.example.bookstore.repository.BookRepository;
import dk.lundogbendsen.springbootcourse.testing.example.bookstore.repository.OrderLineRepository;
import dk.lundogbendsen.springbootcourse.testing.example.bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderLineRepository orderLineRepository;

    public void save(Order order) {
        order.setCreationDate(new Date());
        for (OrderLine orderLine : order.getOrderLines()) {
            bookRepository.findById(orderLine.getBook().getId())
                          .ifPresent(orderLine::setBook);

            orderLineRepository.save(orderLine);
        }
        orderRepository.save(order);
    }

}
