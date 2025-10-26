package dk.lundogbendsen.aop.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CallStackMonitorAdvice {
    ThreadLocal<CallStackMonitorService> threadLocal = new ThreadLocal<>();

    @Around("bean(*Service)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        CallStackMonitorService callStackMonitorService = threadLocal.get();
        boolean isRoot = false;
        if (callStackMonitorService == null) {
            isRoot = true;
            callStackMonitorService = new CallStackMonitorService();
            threadLocal.set(callStackMonitorService);
        }
        callStackMonitorService.enterMethod();
        Object proceed = joinPoint.proceed();
        callStackMonitorService.exitMethod();

        if (isRoot) {
            System.out.println("Around Advice: CallStack maxDepth for service " + joinPoint.getSignature().getName() + " was: " + callStackMonitorService.getMaxDepth());
            threadLocal.remove();
        }
        return proceed;
    }


    private class CallStackMonitorService {

        private int maxDepth;
        private int currentDepth;


        public void enterMethod() {
            currentDepth++;
            if (currentDepth > maxDepth) {
                maxDepth = currentDepth;
            }
        }
        public void exitMethod() {
            currentDepth--;
        }


        public int getMaxDepth() {
            return maxDepth;
        }

        public int getCurrentDepth() {
            return currentDepth;
        }
    }

}
