# Configuration

## Exercises

### Exercise 1. Provide an external configuration file which overrides
app.configIdentifier=file:/application.properties

#### Solution
1. copy the src/main/resources/application.properties to the root folder of the project
2. edit the new file and set the key app.configIdentifier=file:/application.properties
3. run the application from the command line using mvn spring-boot:run



### Exercise 2. Use an environment variable to override
app.configIdentifier=env

#### Solution
1. declare an environment variable using set APP_CONFIGIDENTIFIER=env
2. run the application from the command line using mvn spring-boot:run


### Exercise 3. Provide a Bean belonging to the profile demo

1. Create a new class called DemoService, and annotate it with @Service. 
2. Add a method called demoMethod that prints "DemoBean created" and annotate that method with **@PostConstruct**
3. Add @Profile("demo") to the DemoService class. This will make the bean only available when the "demo" profile is active.

Start the application and verify that the bean is NOT created, since the "demo" profile is not active. You can look in the logs for the message "DemoBean created". You will not find it.

#### Solution

```java
@Service
@Profile("demo")
public class DemoService {

    @PostConstruct
    public void getHelloMessage() {
        System.out.println("Hello from DemoService");
    }
}
```



### Exercise 4. Activate the demo profile
1. Activate the demo profile. There are several ways of doing that. One way is to set the environment variable SPRING_PROFILES_ACTIVE=demo
2. Run the application and verify that the bean is created. You should see the message "DemoBean created" in the logs.


#### Solution
1. `export SPRING_PROFILES_ACTIVE=demo`
2. run the application with `mvn spring-boot:run`

or

1. run the application directly with `SPRING_PROFILES_ACTIVE=demo mvn spring-boot:run`
