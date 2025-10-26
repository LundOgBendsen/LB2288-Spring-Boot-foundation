package dk.lundogbendsen.urlshortener.service;

import dk.lundogbendsen.urlshortener.model.Token;
import dk.lundogbendsen.urlshortener.model.User;
import dk.lundogbendsen.urlshortener.repository.TokenRepository;
import dk.lundogbendsen.urlshortener.service.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Service
public class TokenService {
    @Autowired
    TokenRepository tokenRepository;

    public List<Token> listUserTokens(User user) {
        final List<Token> userTokens = tokenRepository.findAllByUser(user);
        return userTokens;
    }

    public void deleteTokens(User user) {
        tokenRepository.deleteAllByUser(user);
    }

    public Token create(String theToken, String targetUrl, String protectToken, User user) {
        if (theToken.equals("token")) {
            throw new IllegalTokenNameException();
        }
        if (tokenRepository.existsById(theToken)) {
            throw new TokenAlreadyExistsException();
        }
        if (targetUrl == null) {
            throw new TokenTargetUrlIsNullException();
        }
        if (targetUrl.contains("localhost")) {
            throw new IllegalTargetUrlException();
        }
        try {
            new URI(targetUrl);
        } catch (URISyntaxException e) {
            throw new InvalidTargetUrlException();
        }

        final Token token = Token.builder().token(theToken).targetUrl(targetUrl).protectToken(protectToken).user(user).build();
        return tokenRepository.save(token);
    }

    public Token update(String theToken, String targetUrl, String protectToken, User user) {
        final Token token = tokenRepository.findById(theToken).orElseThrow(TokenNotFoundException::new);

        if (!token.getUser().getUsername().equals(user.getUsername())) {
            throw new AccessDeniedException();
        }
        if (targetUrl == null) {
            targetUrl = token.getTargetUrl();
        }
        if (targetUrl.contains("localhost")) {
            throw new IllegalTargetUrlException();
        }
        try {
            new URI(targetUrl);
        } catch (URISyntaxException e) {
            throw new InvalidTargetUrlException();
        }
        token.setTargetUrl(targetUrl);
        token.setProtectToken(protectToken);
        return tokenRepository.save(token);
    }

    public void deleteToken(String theToken, User user) {
        tokenRepository.deleteByTokenAndUser(theToken, user);
    }

    public String resolveToken(String theToken, String protectToken) {
        final Token token = tokenRepository.findByTokenAndProtectToken(theToken, protectToken).orElseThrow(TokenNotFoundException::new);
        return token.getTargetUrl();
    }

    public Token getToken(String theToken, User user) {
        final Token token = tokenRepository.findByTokenAndUser(theToken, user).orElseThrow(TokenNotFoundException::new);
        return token;
    }
}
