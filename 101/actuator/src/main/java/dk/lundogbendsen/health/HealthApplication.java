package dk.lundogbendsen.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HealthApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean<MyMetricsTimerFilter> filterRegistrationBean(MyMetricsTimerFilter myMetricsTimerFilter) {
        return new FilterRegistrationBean<>(myMetricsTimerFilter);
    }
}
