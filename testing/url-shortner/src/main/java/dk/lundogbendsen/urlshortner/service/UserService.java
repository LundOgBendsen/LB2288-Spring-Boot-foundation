package dk.lundogbendsen.urlshortner.service;

import dk.lundogbendsen.urlshortner.model.User;
import dk.lundogbendsen.urlshortner.service.exceptions.UserExistsException;
import dk.lundogbendsen.urlshortner.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserService {
    @Autowired
    private TokenService tokenService;
    private HashMap<String, User> users = new HashMap<>();

    public User create(String userName, String password) {
        if (users.containsKey(userName)) {
            throw new UserExistsException();
        }
        final User user = User.builder().username(userName).password(password).build();
        users.put(userName, user);
        return user;
    }

    public void delete(String userName) {
        final User user = users.get(userName);
        if (user != null) {
            users.remove(userName);
            tokenService.deleteTokens(user);
        }
    }

    public User getUser(String userName) {
        if (!users.containsKey(userName)) {
            throw new UserNotFoundException();
        }
        return users.get(userName);
    }
}
