package dk.lundogbendsen.aop.examples;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Example 3: @AfterReturning Advice
 *
 * @AfterReturning advice executes ONLY IF the matched method returns normally without throwing an exception.
 * It can access the returned value by binding it to an advice method parameter via 'returning'.
 * Commonly used for auditing success results, caching, or post-processing return values.
 */
@Aspect
@Component
public class AfterReturningAdviceExample {

    /**
     * Pointcut matching DummyService.calculateDiscount(..).
     */
    @Pointcut("execution(* dk.lundogbendsen.aop.examples.DummyService.calculateDiscount(..))")
    public void calculateDiscountPointcut() {}

    /**
     * Advice method running after calculateDiscount returns successfully.
     * The 'returning' attribute binds the return value to the 'result' parameter.
     */
    @AfterReturning(pointcut = "calculateDiscountPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("[@AfterReturning Advice] Method: " + methodName + " successfully returned: " + result);
    }
}
