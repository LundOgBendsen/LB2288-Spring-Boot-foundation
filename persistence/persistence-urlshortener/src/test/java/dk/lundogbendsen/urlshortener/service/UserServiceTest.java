package dk.lundogbendsen.urlshortener.service;

import dk.lundogbendsen.urlshortener.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserServiceTest {
    @Autowired UserService userService;

    @Test
    public void createUserTest() {
        final User user = userService.create("user", "password");
        final User getUser = userService.getUser("user");
        assertEquals("password", getUser.getPassword());
    }
}