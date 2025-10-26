package dk.lundogbendsen.properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ProfiledBeans {
    @Bean
    @Profile("!Test") // The @Profile annotation can be added directly to a @Bean method to indicate that the bean should only be created if the specified profile is active.
    public String profiledBean() {
        return "ProfiledBean";
    }

    @Bean // This bean implicitly belongs to the "default" profile and is always created.
    public String defaultBean() {
        return "DefaultBean";
    }
}
