package dk.lundogbendsen.health;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MyMetricsController {
    @Autowired
    private MeterRegistry meterRegistry;

    @PostMapping
    public void sendData(@RequestBody String data) {
        meterRegistry.counter("content-size", "unit", "chars").increment(data.length());
    }

    @GetMapping
    public String get() {
        meterRegistry.counter("Hello World called", "unit", "times").increment();
        log.info("Called get");
        return "Hello World";
    }

}
