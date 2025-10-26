package dk.lundogbendsen.health;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class MyMetricsTimerFilter implements Filter {
    @Autowired
    private MeterRegistry meterRegistry;

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        // Create timer (or reuse one that was already created)
        final Timer timer = meterRegistry.timer("http.processtime",
                "method", ((HttpServletRequest) servletRequest).getMethod(),
                "path", ((HttpServletRequest) servletRequest).getRequestURI()
        );

        final long start = System.nanoTime();
        filterChain.doFilter(servletRequest, servletResponse);
        final long end = System.nanoTime();

        // Add an Observation that is tagged with method and path
        timer.record((end-start), TimeUnit.NANOSECONDS);
    }
}
