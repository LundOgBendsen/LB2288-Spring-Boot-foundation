package dk.lundogbendsen.springbootcourse.testing.bookstore;


import dk.lundogbendsen.springbootcourse.testing.example.bookstore.controller.OrderController;
import dk.lundogbendsen.springbootcourse.testing.example.bookstore.repository.BookRepository;
import dk.lundogbendsen.springbootcourse.testing.example.bookstore.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@ExtendWith(MockitoExtension.class)
public class TestBookStoreApi {
    private MockMvc mockMvc;

    private BookRepository bookRepository = mock(BookRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setup() {
        // MockMvc standalone approach
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .build();
    }

    @Test
    @DisplayName("when I request the list of all Orders, I get all Orders")
    public void testGetAllOrders() throws Exception {
        mockMvc.perform(get("/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Content-Type"));
    }
}
