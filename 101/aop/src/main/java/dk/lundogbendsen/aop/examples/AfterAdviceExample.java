package dk.lundogbendsen.aop.examples;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Example 2: @After (Finally) Advice
 *
 * @After advice executes AFTER the matched method finishes, regardless of whether
 * it returned normally or threw an exception (equivalent to a 'finally' block).
 * Commonly used for resource cleanup, releasing locks, or final audit logging.
 */
@Aspect
@Component
public class AfterAdviceExample {

    /**
     * Pointcut matching any method execution in DummyService.
     */
    @Pointcut("execution(* dk.lundogbendsen.aop.examples.DummyService.*(..))")
    public void anyDummyServiceMethod() {}

    /**
     * Advice method running after any DummyService method completes (success or failure).
     */
    @After("anyDummyServiceMethod()")
    public void logAfter(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("[@After (Finally) Advice] Completed method: " + methodName + " (executed regardless of outcome)");
    }
}
