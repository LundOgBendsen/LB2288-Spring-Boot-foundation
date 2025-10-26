package dk.lundogbendsen.urlshortener.repository;

import dk.lundogbendsen.urlshortener.model.Token;
import dk.lundogbendsen.urlshortener.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class TokenRepositoryTest {
    @Autowired UserRepository userRepository;
    @Autowired TokenRepository tokenRepository;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void init() {
        user1 = userRepository.findById("user1").get();
        user2 = userRepository.findById("user2").get();
        user3 = userRepository.findById("user3").get();
    }

    @Test
    public void findAllByUserTest() {
        final List<Token> allByUser = tokenRepository.findAllByUser(user3);
        assertEquals(3, allByUser.size());
        assertEquals("token5", allByUser.get(0).getToken());
        assertEquals("token6", allByUser.get(1).getToken());
        assertEquals("token7", allByUser.get(2).getToken());
    }
    @Test
    public void findAllByTokenAndUserTest() {
        final Optional<Token> token = tokenRepository.findByTokenAndUser("token1", user1);
        assertTrue(token.isPresent());
        assertEquals("https://dr.dk", token.get().getTargetUrl());
    }
    @Test
    public void findAllByTokenAndProtectTokenTest() {
        final Optional<Token> token = tokenRepository.findByTokenAndProtectToken("token4", "pt2");
        assertTrue(token.isPresent());
        assertEquals("https://dr.dk", token.get().getTargetUrl());
        assertEquals(user2, token.get().getUser());
    }
    @Test
    public void deleteAllByUserTest() {
        tokenRepository.deleteAllByUser(user3);
        final List<Token> allByUser = tokenRepository.findAllByUser(user3);
        assertEquals(0, allByUser.size());
    }
    @Test
    public void deleteByTokenAndUserTest() {
        tokenRepository.deleteByTokenAndUser("token1", user1);
        final List<Token> allByUser = tokenRepository.findAllByUser(user1);
        assertEquals(0, allByUser.size());
    }
}