package dk.lundogbendsen.urlshortener.service;

import dk.lundogbendsen.urlshortener.model.User;
import dk.lundogbendsen.urlshortener.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class TokenServiceTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenService tokenService;
    private User user;

    @BeforeEach
    public void init() {
        user = userRepository.findById("user1").get();
    }

    @Test
    @DisplayName("create token with the name 'token' (fails)")
    public void testCreateTokenWithTheNameToken() {
        try {
            tokenService.create("token", "https://dr.dk", null, user);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    @DisplayName("create token that already exists (fails)")
    public void testCreateTokenThatAlreadExists() {
        try {
            tokenService.create("token1", "https://dr.dk", null, user);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    @DisplayName("create token without a targetUrl (fails)")
    public void testCreateTokenWithoutTargetUrl() {
        try {
            tokenService.create("token1", null, null, user);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    @DisplayName("create token with an invalid targetUrl (fails)")
    public void testCreateTokenWithInvalidTargetUrl() {
        try {
            tokenService.create("token1", "//dr.dk", null, user);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    @DisplayName("create token with a targetUrl containing localhost (fails)")
    public void testCreateTokenWithTargetUrlContainingLocalhost() {
        try {
            tokenService.create("token1", "http://localhost:8080/abc", null, user);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    @DisplayName("create token with a legal targetUrl (success)")
    public void testCreateTokenWithLocalTargetUrl() {
        tokenService.create("abc", "https://dr.dk", "pt1", user);
    }
}