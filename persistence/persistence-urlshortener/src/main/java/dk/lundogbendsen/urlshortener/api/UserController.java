package dk.lundogbendsen.urlshortener.api;

import com.fasterxml.jackson.databind.JsonNode;
import dk.lundogbendsen.urlshortener.model.User;
import dk.lundogbendsen.urlshortener.security.SecurityContext;
import dk.lundogbendsen.urlshortener.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired private UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@RequestBody JsonNode body) {
        final String userName = body.get("userName").asText();
        final String password = body.get("password").asText();
        final User user = userService.create(userName, password);
        return userName;
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete() {
        userService.delete(SecurityContext.getUser().getUsername());
    }
}
