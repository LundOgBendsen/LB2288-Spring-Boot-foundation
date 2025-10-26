package dk.lundogbendsen.scheduled;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScheduledApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduledApplication.class, args);
    }


    @Bean
    public ScheduledService scheduledService() {
        System.out.println("Preparing light Job Bean");
        return new ScheduledService();
    }

//    @Bean
    public HeavyJobService heavyJobService() {
        System.out.println("Preparing HEAVY Job Bean");
        return new HeavyJobService();
    }

}
