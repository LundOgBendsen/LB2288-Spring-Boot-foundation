# Spring-Primer

## Exercises

Solutions are available in [SOLUTIONS.md](SOLUTIONS.md).

### SECTION 1 - Exercise 1 - investigate the Spring Context

- Create a naked Springboot application called SpringPrimer.
- Outcomment the @SpringBootApplication annotation.
- Set the curser on the run method and to extract a variable called context (Ctrl + Alt + V).
- Set a breakpoint on the line.
- Debug the application (Shift + F9) and use the context to inspect the beans - how many beans are there, and what kinds
  are they?

### SECTION 1 - Exercise 2 - Make a Bean with the @Bean-option, aka "Java Configuration"

- Create a class TextUpperCaser class that have a method that takes a String and returns the String UPPERCASED.
- Register TextUpperCaser as a SpringBean using the @Bean option.
- Retrieve the TextUpperCaser bean and use it.

### SECTION 1 - Exercise 3 - Make a Bean with the @Service option, aka "Annotation Configuration"

- Modify the TextUpperCaser to be a @Service.
- Add @ComponentScan to the SpringPrimerApplication class.
- Retrieve the TextUpperCaser bean and use it.

### SECTION 1 - Exercise 4 - Wiring using Option 1 (@Autowired)

- Create two classes, ServiceA and ServiceB, both without any methods.
- Make both of them Beans by using one of the previous methods.
- In ServiceA create a class field of type ServiceB.
- Use @Autowired to instruct Spring to inject ServiceB.
- Use the ApplicationContext to get a handle to the ServiceA bean.
- Set a breakpoint on that line.
- Run the application in debug mode.
- Inspect the ServiceA handle and see if the ServiceB field has been injected.

### SECTION 1 - Exercise 5 - Wiring using Option 2 (Constructor injection)

- Create two classes, ServiceA and ServiceB, both without any methods.
- Make both of them Beans by using one of the previous methods.
- In ServiceA create a final class field of type ServiceB.
- In ServiceA create a constructor receiving ServiceB.
- Use the ApplicationContext to get a handle to the ServiceA bean.
- Set a breakpoint on that line.
- Run the application in debug mode.
- Inspect the ServiceA handle and see if the ServiceB field has been injected.


**NB: below is section 2. Did the slides ask you to do this exercise?**
### SECTION 2 - Exercise 6 - Curated list of dependencies - find the version of a dependency

In this exercise, you'll inspect the list for dependencies to find out exactly with the version of Spring Core we are
using.

- Open the pom.xml.
- Use Intellij to navigate to the Parent pom.
  - This is done by right-clicking on the pom.xml and selecting "Maven" → "Show effective POM".
- Here you'll find a lot of XML snippets in the structure:

```
<properties>
...
</properties>
<dependencyManagement>
  <dependencies>
  ...
  </dependencies>
</dependencyManagement>
```

- Search for "rabbit".
- What is the version of the RabbitMQ client?

### SECTION 2 - Exercise 7 - Auto configuration

`@SpringBootApplication` includes `@EnableAutoConfiguration`. During startup, Spring Boot uses this to discover
auto-configuration classes and apply the ones whose conditions match the application, such as classes available on the
classpath and beans that have already been defined.

This course uses Spring Boot 4. In Spring Boot 3 and later, auto-configuration
class names are listed in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` in the
`spring-boot-autoconfigure` dependency. The older `spring.factories` mechanism was used before Spring Boot 3.

1. Open `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` in the
   `spring-boot-autoconfigure` dependency. Use **Navigate | File** (`Ctrl + Shift + N`) and, if needed, search again
   with dependency files included.
2. Find `JmxAutoConfiguration` in the list and open the class.
3. Look at its `@AutoConfiguration` and `@ConditionalOn...` annotations. Consider what each condition checks and why
   auto-configuration should back off when the application has already supplied a bean.
```

This is an example of the general pattern: a starter brings relevant libraries onto the classpath, and conditional
auto-configuration creates default beans when its requirements are met. If you define a bean yourself, a
`@ConditionalOnMissingBean` condition can let your bean take precedence. The exact implementation can change between
Spring Boot versions.
