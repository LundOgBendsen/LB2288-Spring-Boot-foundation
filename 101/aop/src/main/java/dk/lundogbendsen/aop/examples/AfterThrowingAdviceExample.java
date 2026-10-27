package dk.lundogbendsen.aop.examples;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Example 4: @AfterThrowing Advice
 *
 * @AfterThrowing advice executes ONLY IF the matched method throws an exception.
 * It can access the thrown exception by binding it to an advice method parameter via 'throwing'.
 * Commonly used for error logging, notifications/alerts, or transaction rollback logging.
 */
@Aspect
@Component
public class AfterThrowingAdviceExample {

    /**
     * Pointcut matching DummyService.transferFunds(..).
     */
    @Pointcut("execution(* dk.lundogbendsen.aop.examples.DummyService.transferFunds(..))")
    public void transferFundsPointcut() {}

    /**
     * Advice method running when transferFunds throws an exception.
     * The 'throwing' attribute binds the exception to the 'exception' parameter.
     */
    @AfterThrowing(pointcut = "transferFundsPointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("[@AfterThrowing Advice] Exception caught from method: " + methodName + " -> " + exception.getClass().getSimpleName() + ": " + exception.getMessage());
    }
}
