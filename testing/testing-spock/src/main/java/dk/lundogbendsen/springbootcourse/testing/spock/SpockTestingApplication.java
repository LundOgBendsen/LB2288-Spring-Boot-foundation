package dk.lundogbendsen.springbootcourse.testing.spock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "dk.lundogbendsen.sprinbootcourse.persistence.education",
        "dk.lundogbendsen.springbootcourse.testing.spock"
})
@EnableJpaRepositories(basePackages = "dk.lundogbendsen.sprinbootcourse.persistence.education.repository")
@EntityScan(basePackages = "dk.lundogbendsen.sprinbootcourse.persistence.education.model")
@EnableJpaAuditing
public class SpockTestingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpockTestingApplication.class, args);
    }
}
