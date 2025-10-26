package dk.lundogbendsen.springbootcourse.testing.api;

import jakarta.servlet.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("In Filter");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
