package dk.lundogbendsen.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Slf4j
public class MyIntercepter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("In PreHandle 1");
        MDC.put("TraceId", UUID.randomUUID().toString());
        log.debug("In PreHandle 2");
        return true;
    }

    @Override
    /**
     * postHandler is NOT called in case of exception in controller
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.debug("All good - no exceptions in Controller!");
    }

    @Override
    /**
     * Is always called even in case of Exceptions
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.debug("In PostHandle 1");
        MDC.clear();
        log.debug("In PostHandle 2");
    }
}
