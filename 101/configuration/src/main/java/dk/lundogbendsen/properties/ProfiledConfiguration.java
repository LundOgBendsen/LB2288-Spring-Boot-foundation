package dk.lundogbendsen.properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("Test") // The @Profile annotation can be added to a class to indicate that ALL beans in the class should only be created if the specified profile is active.
public class ProfiledConfiguration {
    @Bean
    public String profiledBean() {
        return "ProfiledBean";
    }
}
