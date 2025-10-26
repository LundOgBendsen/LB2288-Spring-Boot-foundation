package dk.lundogbendsen.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfigValue {
    @Value("${app.name:NoName}")
    public String appName;

    @Value("${app.configIdentifier:Default}")
    public String configurationId;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AppConfiguration{");
        sb.append("appName='").append(appName).append('\'');
        sb.append(", configurationId='").append(configurationId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
