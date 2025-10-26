package dk.lundogbendsen.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "myapp")
public class AppConfigProperties {
    String databaseEndpoint;

    public void setDatabaseEndpoint(String databaseEndpoint) {
        this.databaseEndpoint = databaseEndpoint;
    }
}
