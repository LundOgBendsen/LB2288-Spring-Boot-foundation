package dk.lundogbendsen.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PropertiesApplication {

    Logger logger = LoggerFactory.getLogger(PropertiesApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PropertiesApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(AppConfigValue appConfigValue, AppConfigProperties appConfigProperties) {
        return args -> {
            logger.info("Current AppConfiguration: {}", appConfigValue);
            logger.info("Current AppConfigProperties: {}", appConfigProperties.databaseEndpoint);
        };
    }
}
