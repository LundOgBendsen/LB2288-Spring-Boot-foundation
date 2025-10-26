package dk.lundogbendsen.aop.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class CallStackPrinterAdvice {
    ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    @Around("bean(*Service)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Integer indentation = threadLocal.get();
        if (indentation == null) {
            indentation = 0;
            threadLocal.set(indentation);
        }
        System.out.println(" ".repeat(indentation*4) +  "ENTER " + joinPoint.getSignature().getName() + "(" + Arrays.asList(joinPoint.getArgs()) + ")");
        threadLocal.set(indentation + 1);
        Object proceed = joinPoint.proceed();
        threadLocal.set(indentation);
        return proceed;
    }
}
