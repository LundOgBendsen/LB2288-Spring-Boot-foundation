package dk.lundogbendsen.properties;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("demo")
public class DemoService {

    @PostConstruct
    public void getHelloMessage() {
        System.out.println("Hello from DemoService");
    }
}
