package dk.lundogbendsen.urlshortener.service;

import dk.lundogbendsen.urlshortener.model.User;
import dk.lundogbendsen.urlshortener.repository.UserRepository;
import dk.lundogbendsen.urlshortener.service.exceptions.UserExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private TokenService tokenService;
    @Autowired
    UserRepository userRepository;

    public User create(String userName, String password) {
        Optional<User> exitingUser = userRepository.findById(userName);
        if (exitingUser.isPresent()) {
            throw new UserExistsException();
        }
        User user = User.builder().username(userName).password(password).build();
        userRepository.save(user);
        return user;
    }

//    @Transactional
    public void delete(String userName) {
        Optional<User> user = userRepository.findById(userName);
        if (user.isPresent()) {
            tokenService.deleteTokens(user.get());
            throw new RuntimeException();
        }
        userRepository.delete(user.get());
    }

    public User getUser(String userName) {
        return userRepository.findById(userName).orElse(null);
    }
}
