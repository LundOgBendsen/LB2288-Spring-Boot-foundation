# Spring-Primer Solutions

Solutions for the exercises in [README.md](README.md).

### Exercise 1 - investigate the Spring Context

```
// @SpringBootApplication
public class SpringPrimerApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(SpringPrimerApplication.class, args);
    }
}
```

### Exercise 2 - Make a Bean with the @Bean-option, aka "Java Configuration"

The TextUpperCaser class:
```
public class TextUpperCaser {
    public String toUpperCase(String message) {
        return message.toUpperCase();
    }
}
```

The SpringPrimerApplication:
```
@Configuration
public class SpringPrimerApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(SpringPrimerApplication.class, args);
        final TextUpperCaser bean = context.getBean(TextUpperCaser.class);
        System.out.println(bean.toUpperCase("Hello World"));
    }

    @Bean
    public TextUpperCaser textUpperCaser() {
        return new TextUpperCaser();
    }
}
```

### Exercise 3 - Make a Bean with the @Service option, aka "Annotation Configuration"

The TextUpperCaser class:
```
@Service
public class TextUpperCaser {
    public String toUpperCase(String message) {
        return message.toUpperCase();
    }
}
```

The SpringPrimerApplication:
```
@ComponentScan
public class SpringPrimerApplication {
    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(SpringPrimerApplication.class, args);
        final TextUpperCaser bean = context.getBean(TextUpperCaser.class);
        System.out.println(bean.toUpperCase("Hello World"));
    }
}
```

### Exercise 4 - Wiring using Option 1 (@Autowired)

ServiceA and ServiceB:
```
@Component
public class ServiceA {
    @Autowired
    ServiceB serviceB;

    public void callB() {
        serviceB.bMethod();
    }
}

@Component
public class ServiceB {
    public void bMethod() {
        System.out.println("ServiceB - HelloWorld");
    }
}
```

SpringPrimerApplication:
```
@ComponentScan
public class SpringPrimerApplication {
    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(SpringPrimerApplication.class, args);
        final ServiceA serviceA = context.getBean(ServiceA.class);
        serviceA.callB();
    }
}
```

### Exercise 5 - Wiring using Option 2 (Constructor injection)

ServiceA and ServiceB:
```
@Component
public class ServiceA {
    ServiceB serviceB;

    public ServiceA(ServiceB serviceB) {
        this.serviceB = serviceB;
    }

    public void callB() {
        serviceB.bMethod();
    }
}

@Component
public class ServiceB {
    public void bMethod() {
        System.out.println("ServiceB - HelloWorld");
    }
}
```

SpringPrimerApplication:
```
@ComponentScan
public class SpringPrimerApplication {
    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(SpringPrimerApplication.class, args);
        final ServiceA serviceA = context.getBean(ServiceA.class);
        serviceA.callB();
    }
}
```