package dk.lundogbendsen.sprinbootcourse.persistence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableJpaAuditing
public class PersistenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersistenceApplication.class, args);
    }

}
