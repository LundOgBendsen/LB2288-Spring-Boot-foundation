package dk.lundogbendsen.urlshortner.controller;

import dk.lundogbendsen.urlshortner.model.Token;
import dk.lundogbendsen.urlshortner.model.User;
import dk.lundogbendsen.urlshortner.service.TokenService;
import dk.lundogbendsen.urlshortner.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UrlShortnerController {

    private final UserService userService;
    private final TokenService tokenService;

    public UrlShortnerController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.create(user.getUsername(), user.getPassword());
    }

    @GetMapping("/users/{username}")
    public User getUser(@PathVariable String username) {
        return userService.getUser(username);
    }

    @PostMapping("/tokens")
    @ResponseStatus(HttpStatus.CREATED)
    public Token createToken(@RequestBody Token token) {
        return tokenService.create(token.getToken(), token.getTargetUrl(), token.getProtectToken(), token.getUser());
    }

    @GetMapping("/tokens/{theToken}")
    public String resolveToken(@PathVariable String theToken) {
        return tokenService.resolveToken(theToken);
    }

    @GetMapping("/tokens/user/{username}")
    public List<Token> listUserTokens(@PathVariable String username) {
        User user = userService.getUser(username);
        return tokenService.listUserTokens(user);
    }

    @DeleteMapping("/tokens/{theToken}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteToken(@PathVariable String theToken, @RequestParam String username) {
        User user = userService.getUser(username);
        tokenService.deleteToken(theToken, user);
    }
}
