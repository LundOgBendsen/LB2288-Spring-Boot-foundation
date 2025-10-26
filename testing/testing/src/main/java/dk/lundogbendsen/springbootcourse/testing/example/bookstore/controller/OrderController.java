package dk.lundogbendsen.springbootcourse.testing.example.bookstore.controller;

import dk.lundogbendsen.springbootcourse.testing.example.bookstore.exception.ResourceNotFoundException;
import dk.lundogbendsen.springbootcourse.testing.example.bookstore.model.Order;
import dk.lundogbendsen.springbootcourse.testing.example.bookstore.repository.OrderRepository;
import dk.lundogbendsen.springbootcourse.testing.example.bookstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    private OrderService orderService;

    private OrderRepository orderRepository;

    @Autowired
    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    Iterable<Order> findAll() {
        return orderRepository.findAll();
    }

    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    void createOperation(@RequestBody Order order) {
        orderService.save(order);
    }

    @RequestMapping(value = "/orders/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {

        Order order = findOrderOrThrowException(id);
        orderRepository.delete(order);
    }

    private Order findOrderOrThrowException(@PathVariable Long id) {
        return orderRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }


    @RequestMapping(value = "/orders/{id}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    Order get(@PathVariable Long id) {
        return findOrderOrThrowException(id);
    }

    @RequestMapping(value = "/orders/{id}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    void editOrder(@RequestBody Order order, @PathVariable Long id) {

        findOrderOrThrowException(id);
        order.setId(id);
        orderRepository.save(order);

    }


}