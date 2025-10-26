package dk.lundogbendsen.urlshortener.api;

import dk.lundogbendsen.urlshortener.model.Token;
import dk.lundogbendsen.urlshortener.security.SecurityContext;
import dk.lundogbendsen.urlshortener.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/token")
public class TokenController {
    @Autowired private TokenService tokenService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Token> list() {
        return tokenService.listUserTokens(SecurityContext.getUser());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Map<String, String> body) {
        final String token = body.get("token");
        final String targetUrl = body.get("targetUrl");
        final String protectToken = body.get("protectToken");
        tokenService.create(token, targetUrl, protectToken, SecurityContext.getUser());
    }

    @PutMapping("/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String token, @RequestBody Map<String, String> body) {
        final String targetUrl = body.get("targetUrl");
        final String protectToken = body.get("protectToken");
        tokenService.update(token, targetUrl, protectToken, SecurityContext.getUser());
    }

    @DeleteMapping("/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String token) {
        tokenService.deleteToken(token, SecurityContext.getUser());
    }

    @PutMapping("/{token}/protect")
    @ResponseStatus(HttpStatus.CREATED)
    public void protect(@PathVariable("token") String theToken, @RequestBody Map<String, String> body) {
        final Token token = tokenService.getToken(theToken, SecurityContext.getUser());
        String protectToken = body.get("protectToken");
        tokenService.update(theToken, token.getTargetUrl(), protectToken, SecurityContext.getUser());
    }
}
