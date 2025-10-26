package dk.lundogbendsen.lombok;

import dk.lundogbendsen.lombok.service.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class LombokApplication {

    public static void main(String[] args) {
        SpringApplication.run(LombokApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(CustomerService customerService) {
        return args -> customerService.create("Christian", 1L, "cvw@example.com", new Date());
    }
}
