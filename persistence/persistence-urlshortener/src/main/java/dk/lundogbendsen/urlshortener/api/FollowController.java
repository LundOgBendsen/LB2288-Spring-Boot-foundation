package dk.lundogbendsen.urlshortener.api;

import dk.lundogbendsen.urlshortener.security.SecurityContext;
import dk.lundogbendsen.urlshortener.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FollowController {
    @Autowired
    TokenService tokenService;

    @GetMapping("{token}")
    public ResponseEntity<Object> follow(@PathVariable String token) {
        final String targetUrl = tokenService.resolveToken(token, SecurityContext.getProtectToken());
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, targetUrl).build();
    }
}
