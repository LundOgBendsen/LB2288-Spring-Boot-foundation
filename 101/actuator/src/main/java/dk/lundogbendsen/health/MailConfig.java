package dk.lundogbendsen.health;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("app.mail")
@Setter
@Getter
public class MailConfig {
    String smtpServer;
    int port = 25;
    String username;
    String password;
}
