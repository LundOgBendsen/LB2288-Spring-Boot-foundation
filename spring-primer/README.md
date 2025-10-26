# Spring-Primer

## Exercises

### Exercise 1 - investigate the Spring Context

- Create a naked Springboot application called SpringPrimer.
- Outcomment the @SpringBootApplication annotation.
- Set the curser on the run method and to extract a variable called context (Ctrl + Alt + V).
- Set a breakpoint on the line.
- Debug the application (Shift + F9) and use the context to inspect the beans - how many beans are there, and what kinds are they?

#### Solution:
```
// @SpringBootApplication
public class SpringPrimerApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(SpringPrimerApplication.class, args);
    }
}
```

### Exercise 2 - Make a Bean with the @Bean-option, aka "Java Configuration"
- Create a class TextUpperCaser class that have a method that takes a String and returns the String UPPERCASED.
- Register TextUpperCaser as a SpringBean using the @Bean option.
- Retrieve the TextUpperCaser bean and use it.
                               
#### Solution:

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
@ComponentScan
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
- Modify the TextUpperCaser to be a @Service.
- Add @ComponentScan to the SpringPrimerApplication class.
- Retrieve the TextUpperCaser bean and use it.

#### Solution

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
- Create two classes, ServiceA and ServiceB, both without any methods.
- Make both of them Beans by using one of the previous methods.  
- In ServiceA create a class field of type ServiceB.
- Use @Autowired to instruct Spring to inject ServiceB.
- Use the ApplicationContext to get a handle to the ServiceA bean.
- Set a breakpoint on that line.
- Run the application in debug mode.
- Inspect the ServiceA handle and see if the ServiceB field has been injected.

#### Solution
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
- Create two classes, ServiceA and ServiceB, both without any methods.
- Make both of them Beans by using one of the previous methods.
- In ServiceA create a class field of type ServiceB.
- In ServiceA create a constructor receiving ServiceB.
- Use the ApplicationContext to get a handle to the ServiceA bean.
- Set a breakpoint on that line.
- Run the application in debug mode.
- Inspect the ServiceA handle and see if the ServiceB field has been injected.

#### Solution
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


### Exercise 6 - Curated list of dependencies - find the version of a dependency
In this exercise, you'll inspect the list for dependencies to find out exactly with the version of Spring Core we are using.

- Open the pom.xml.
- Use Intellij to navigate to the Parent pom.
- From here navigate to the parent's Parent pom.
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
- ?What is the version of the RabbitMQ client?


### Exercise 7 - Auto configuration
Auto configuration kicks in, when Spring detects the @EnableAutoConfiguration annotation.
The annotation enable auto-configuration of the Spring Application Context, attempting to guess and configure beans that you are likely to need. Auto-configuration classes are usually applied based on your classpath and what beans you have defined.

For SpringBoot < 2.7.0:

- Open the `META-INF/spring.factories` of the `spring-boot-autoconfigure` dependency (Ctrl + Shift + N) (You may have to press the combination twice to extend the search into dependencies)
- Find the `# Auto Configure` section (around line 20).
- Here is a key called `org.springframework.boot.autoconfigure.EnableAutoConfiguration` with a multi-line value which becomes to input list to the `AutoConfigurationImportSelector`.

For SpringBoot >= 2.7.0:

The list of configurations to load have been extracted and isolated into its own file, and the loading mechanisms have been refactored.

- Open the `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` of the`spring-boot-autoconfigure` dependency (Ctrl + Shift + N) (You may have to press the combination twice to extend the search into dependencies)

_AutoConfigurationImportSelector_
- The `AutoConfigurationImportSelector` contains the logic for loading the auto-configuration classes referenced in the list of references loaded from either `META-INF/spring.factories` or `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`. 
- Find and open one of the auto-configuration classes `MongoAutoConfiguration`.
- This is how Springboot sets up mongo for you, if you include the

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```
- Below is the content of `MongoAutoConfiguration`: 
```
@AutoConfiguration
@ConditionalOnClass(MongoClient.class)
@EnableConfigurationProperties(MongoProperties.class)
@ConditionalOnMissingBean(type = "org.springframework.data.mongodb.MongoDatabaseFactory")
public class MongoAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(MongoClient.class)
	public MongoClient mongo(ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers,
			MongoClientSettings settings) {
		return new MongoClientFactory(builderCustomizers.orderedStream().collect(Collectors.toList()))
				.createMongoClient(settings);
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnMissingBean(MongoClientSettings.class)
	static class MongoClientSettingsConfiguration {

		@Bean
		MongoClientSettings mongoClientSettings() {
			return MongoClientSettings.builder().build();
		}

		@Bean
		MongoPropertiesClientSettingsBuilderCustomizer mongoPropertiesCustomizer(MongoProperties properties,
				Environment environment) {
			return new MongoPropertiesClientSettingsBuilderCustomizer(properties, environment);
		}

	}

}
```
- It is an example of how the @ConditionalNNN annotations work. In this case the @Beans will be setup if the class `MongoClient` is present on the classpath of the application.
- The MongoClient is part of the `spring-boot-starter-data-mongodb`, so that is why Mongo is working out of the box, just by adding the starter.

**This is how starters work all over!**
