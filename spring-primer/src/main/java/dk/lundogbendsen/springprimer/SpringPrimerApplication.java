package dk.lundogbendsen.springprimer;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

//@SpringBootApplication
@ComponentScan
public class SpringPrimerApplication {
    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(SpringPrimerApplication.class, args);
    }
}
