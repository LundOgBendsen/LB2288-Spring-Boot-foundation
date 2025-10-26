package dk.lundogbendsen.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Api {
    Logger logger = LoggerFactory.getLogger(Api.class);
    @Autowired
    private MyLoggingService myLoggingService;

    @RequestMapping("/api")
    public String api() {
        logger.info("ENTER api()");
        myLoggingService.myMethod();
        logger.info("EXIT api()");
        return MDC.get("TraceId");
    }
}
