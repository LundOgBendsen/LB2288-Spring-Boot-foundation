## Exercises Config-Server

### Exercise 1: Create a git repo

- create a folder on your local disk
- enter the folder and run `git init`
- add new file: application.properties with the property: `server.port=8082`
- commit the file.

### Exercise 2: Create a new Web+ConfigServer project called `Config-Server`

- update the application.properties with the following content:

```properties
server.port=8888
#spring.cloud.config.server.git.uri=git@github.com:cvwjensen/config-server/config-server.git
spring.cloud.config.server.git.uri=file:///tmp/config-repo
```

This will make the config-server listen to port 8888, and access the github repo (eg `cvwjensen/config-server`).

- Also enable the config-server autoconfiguration by adding `@EnableConfigServer` on the main class like:

```
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

### Exercise 3: Start the config server

Now the server should be listening on port 8888 and have access to your repo.

- Test the config-server with `curl localhost:8888/application/default`

You should see a json structure containing the content of your application.properties.

 
## Exercises Client

### 1: Create new Web+Config+Actuator project

Inspect the pom.xml and see that you now have a new section called `<dependencyManagement>` including `spring-cloud-dependencies`. ConfigServer belongs to Spring Cloud stuff.

The dependency for the config client is: 

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

```


Update the application.properties with the following content:

```properties
spring.application.name=<your-well-describing-name>
spring.config.import=optional:configserver:
server.port=8084
```

The `name` property is how the application will identify itself to the config server, so take care to make good naming conventions for your projects in the future.

The `import` property instructs the config client that is should try to pull its configuration from a config-server. The default URI of the server is `http://localhost:8888`, 
and that is why we did not have to specify it. 

Should we want to, update the property like: 

`spring.config.import=optional:configserver:https://URI-of-your-config-server`.

Furthermore, the `optional` part indicates that it's ok not being able to pull a configuration. Then you just continue with any other configuration lying around. 

If you remove `optional`, then the application will not start if it cannot pull a configuration from the config-server.

### 2: Start the client

- What port does the client listen to? It should be port `8082`. 

So the configuration from config-server takes precedence over local configuration!

### 3: Inspect the configuration your clients config using Actuator

- Add the following property:

```properties
management.endpoints.web.exposure.include=*
management.endpoint.env.show-values=always

```

This expopses all endpoint of the Actuator letting you inspect every little detail of the internals of your application.

- Restart the client.
- Open a browser on: `http://localhost:8082/actuator/env`
- Search for a section with `configserver:file`. Here you'll see the configuration that it has available from different property sources. from the config server. The topmost has the most precedence.

### 4: Add a new property file on the repo with the name of your app. Eg: `client1.properties`.

- Specify `server.port=8085`.
- Commit/push the file to the repo.
- Restart your client-app.

Now you should see that it listens to the port you just added.

So this tells us that values from `application.properties` are default values for any client connecting to the config-server. 

But if the config-server can find a specific property file, it's values will take precedence.

### 5: Activate the `demo` profile.

- Add the following line to your local application.properties: `spring.profiles.active=demo`.
- Add a new file to the config-server-repo with the name: `<your-app-name>-demo.properties`.
- In that file you set the `server.port=8086`.
- Restart your application.

Now your application should pull the demo version of the configuration and listen on port 8086.


