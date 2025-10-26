package dk.lundogbendsen.urlshortener.api;

import dk.lundogbendsen.urlshortener.service.exceptions.AccessDeniedException;
import dk.lundogbendsen.urlshortener.service.exceptions.TokenAlreadyExistsException;
import dk.lundogbendsen.urlshortener.service.exceptions.UserExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerAdvicer {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public void handleAccessDenied(AccessDeniedException accessDeniedException) {
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public void handleRuntime(RuntimeException runtimeException) {
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({TokenAlreadyExistsException.class, UserExistsException.class})
    public String handleConflict(Exception runtimeException) {
        return runtimeException.getMessage();
    }
}
