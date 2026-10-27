package dk.lundogbendsen.aop.examples;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Example 1: @Before Advice
 *
 * @Before advice executes BEFORE the matched method is called.
 * It cannot prevent execution of the target method (unless an exception is thrown from the advice).
 * Commonly used for logging method entry, argument validation, and authorization checks.
 */
@Aspect
@Component
public class BeforeAdviceExample {

    /**
     * Pointcut matching DummyService.processOrder(..).
     */
    @Pointcut("execution(* dk.lundogbendsen.aop.examples.DummyService.processOrder(..))")
    public void processOrderPointcut() {}

    /**
     * Advice method running before processOrder executes.
     * JoinPoint provides access to method metadata and arguments.
     */
    @Before("processOrderPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        System.out.println("[@Before Advice] Entering method: " + methodName + " with args: " + Arrays.toString(args));
    }
}
