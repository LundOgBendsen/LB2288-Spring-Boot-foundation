package dk.lundogbendsen.api.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

//@Component
public class SecurityFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        final String apiToken = req.getHeader("apiToken");
        if (apiToken == null) {
            System.out.println("Unauthorized");
            HttpServletResponse res = (HttpServletResponse) response;
            res.sendError(HttpStatus.UNAUTHORIZED.value(), "You shall not pass!");
            return;
        }

        chain.doFilter(request, response);

    }
}
