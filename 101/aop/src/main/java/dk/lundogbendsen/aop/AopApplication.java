package dk.lundogbendsen.aop;

import dk.lundogbendsen.aop.service.FibonacciService;
import dk.lundogbendsen.aop.service.MyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AopApplication {

    public static void main(String[] args) {
        SpringApplication.run(AopApplication.class, args);
    }


    @Bean
    public CommandLineRunner runner(MyService myService) {
        return args -> {
            // Execute business method - how many log statements?
            myService.myTimedMethod(1000);
        };
    }

//    @Bean
    public CommandLineRunner runner(FibonacciService fibonacciService) {
        return args -> {
            int fib = fibonacciService.fib(6);

            System.out.println("fib = " + fib);
        };
    }



}
