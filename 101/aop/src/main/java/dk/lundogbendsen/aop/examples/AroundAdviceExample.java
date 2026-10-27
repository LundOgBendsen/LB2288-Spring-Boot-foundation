package dk.lundogbendsen.aop.examples;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Example 5: @Around Advice
 *
 * @Around advice is the most powerful advice type. It surrounds the target method invocation.
 * It has control over when and whether joinPoint.proceed() is invoked, can inspect and modify arguments,
 * handle or rethrow exceptions, and inspect or modify the return value.
 * Commonly used for execution profiling/timing, caching, retry logic, and security/transactions.
 */
@Aspect
@Component
public class AroundAdviceExample {

    /**
     * Pointcut matching DummyService.generateReport(..).
     */
    @Pointcut("execution(* dk.lundogbendsen.aop.examples.DummyService.generateReport(..))")
    public void generateReportPointcut() {}

    /**
     * Advice method surrounding generateReport execution.
     * ProceedingJoinPoint must be used so proceed() can be called.
     */
    @Around("generateReportPointcut()")
    public Object profileAndModifyResult(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();

        System.out.println("[@Around Advice] Before invoking: " + methodName);

        Object result;
        try {
            // Proceed with the actual target method execution
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            System.out.println("[@Around Advice] Exception in " + methodName + ": " + throwable.getMessage());
            throw throwable;
        }

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("[@Around Advice] After invoking: " + methodName + " (Execution time: " + duration + " ms)");

        // Optionally transform or wrap the returned value
        return result + " [Processed by @Around]";
    }
}
