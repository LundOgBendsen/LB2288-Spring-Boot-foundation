# AOP

## Exercises

Start out by making a naked Springboot Service.

Then add to the pom.xml the AOP starter:

<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-aop</artifactId>
</dependency>


### Exercise 1: Create a Execution Time Advice that logs how long time execution of a method took

1. Make a simple service inspired by `101/aop/src/main/java/dk/lundogbendsen/aop/service/MyService.java` (or just copy that).
2. Make an Around Advice called ExecutionTimeAspect.
   - It should target all bean called *Service 
   - It should mark the time before and after executing the JoinPoint. 
   - Then it should print the difference of the two marks.
3. Make a Spring Bean of type CommandLineRunner that executes the simple service's method.

#### Solution
**Service**: `101/aop/src/main/java/dk/lundogbendsen/aop/service/MyService.java`.

**Advice**: `101/aop/src/main/java/dk/lundogbendsen/aop/aop/MethodExecutionTimerAspect.java`.

**CommandLineRunner**: 
```
@Bean
public CommandLineRunner runner(MyService myService) {
    return args -> {
        // Execute business method
        myService.myTimedMethod(1000);
    };
}
```


### Exercise 2: Print the call stack with indentations

Print the method names of the call stack.

Every time the call stack gets deeper, the indentation gets bigger

Example of calling the fib():

```
ENTER fib([6])
    ENTER fib([5])
        ENTER fib([4])
            ENTER fib([3])
                ENTER fib([2])
                    ENTER fib([1])
                    ENTER fib([0])
                ENTER fib([1])
            ENTER fib([2])
                ENTER fib([1])
                ENTER fib([0])
        ENTER fib([3])
            ENTER fib([2])
                ENTER fib([1])
                ENTER fib([0])
            ENTER fib([1])
    ENTER fib([4])
        ENTER fib([3])
            ENTER fib([2])
                ENTER fib([1])
                ENTER fib([0])
            ENTER fib([1])
        ENTER fib([2])
            ENTER fib([1])
            ENTER fib([0])
```


#### Solution
See `101/aop/src/main/java/dk/lundogbendsen/aop/aop/CallStackPrinterAdvice.java`.
