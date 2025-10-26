package dk.lundogbendsen.urlshortener.repository;

import dk.lundogbendsen.urlshortener.model.Token;
import dk.lundogbendsen.urlshortener.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findAllByUser(User user);
    Optional<Token> findByTokenAndUser(String token, User user);
    Optional<Token> findByTokenAndProtectToken(String token, String protectToken);
    @Transactional
    void deleteAllByUser(User user);
    @Transactional
    void deleteByTokenAndUser(String token, User user);
}
