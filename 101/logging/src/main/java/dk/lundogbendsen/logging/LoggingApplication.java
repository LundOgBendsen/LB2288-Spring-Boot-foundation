package dk.lundogbendsen.logging;

import org.slf4j.MDC;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LoggingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoggingApplication.class, args);
	}

//	@Bean
	public CommandLineRunner exercise1(MyLoggingService myLoggingService) {
		return args -> myLoggingService.myMethod();
	}

//	@Bean
	public CommandLineRunner exercise3(MyLoggingService myLoggingService) {
		MDC.put("userId", "abc");
		return args -> myLoggingService.exercise3();
	}
	@Bean
	public CommandLineRunner exercise4(MyLoggingService myLoggingService) {
		MDC.put("userId", "abc");
		MDC.put("orderId", "123");
		return args -> myLoggingService.exercise4();
	}


	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean bean = new FilterRegistrationBean(new TracingFilter());
		return bean;
	}
}
