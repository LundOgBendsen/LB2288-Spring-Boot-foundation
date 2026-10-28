package dk.lundogbendsen.urlshortner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    String token;
    String protectToken;
    String targetUrl;
    User user;
}
