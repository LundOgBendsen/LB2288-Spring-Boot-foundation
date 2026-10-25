# Person-Service-Starter Solutions

Return to the [exercises](README.md).

### Exercise 3: Add dependencies to project

Content of the `pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>org.example</groupId>
  <artifactId>person-service-starter</artifactId>
  <version>1.0-SNAPSHOT</version>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter</artifactId>
      <version>4.1.2</version>
    </dependency>
    <dependency>
      <groupId>dk.lundogbendsen</groupId>
      <artifactId>person-service-lib</artifactId>
      <version>1.0</version>
      <optional>true</optional>
    </dependency>

  </dependencies>
</project>
```

### Exercise 4: Add Configuration class to produce Spring Bean of Person-Service

```java
@Configuration
public class PersonServiceConfiguration {
    @Bean
    public PersonService personService() {
        return new PersonService();
    }
}
```

### Exercise 5: Make bean creation conditional

```java
@Configuration
@ConditionalOnClass(PersonService.class)
public class PersonServiceConfiguration {

    @Bean
    @ConditionalOnMissingBean(PersonService.class)
    public PersonService personService() {
        return new PersonService();
    }
}
```

### Exercise 6: Register the auto-configuration

Content of `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:

```text
org.example.personservicestarter.PersonServiceConfiguration
```

### Exercise 7: Build and install the project

```text
cd into person-service-starter project.
execute `mvn install`
```

### Exercise 8: Try out the starter in a new Spring Boot project

```java
package com.example.demo;

@SpringBootApplication
public class Demo1Application {
    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(Demo1Application.class, args);
        final PersonService personService = context.getBean(PersonService.class);
        System.out.println("personService.list() = " + personService.list());
    }
}
```

In `pom.xml`, add these two dependencies:

```xml
        <dependency>
            <groupId>dk.lundogbendsen</groupId>
            <artifactId>person-service-lib</artifactId>
            <version>1.0</version>
        </dependency>
        <dependency>
            <groupId>org.example</groupId>
            <artifactId>person-service-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```