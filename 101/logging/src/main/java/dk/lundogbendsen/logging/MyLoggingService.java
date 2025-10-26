package dk.lundogbendsen.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class MyLoggingService {
    Logger log = LoggerFactory.getLogger(MyLoggingService.class);

    public void myMethod() {
        log.debug("Hello World");
    }

    public void exercise3() {
        log.debug("Hello World from user: {}", MDC.get("userId") );
    }

    public void exercise4() {
        log.debug("Hello World");
    }
}
