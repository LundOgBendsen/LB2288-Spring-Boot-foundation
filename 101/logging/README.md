# Logging

## Exercises

Create a naked Springboot project.


### Exercise 1: Set log level to debug on package dk
- Create a Service in package dk.lundogbendsen.
- Create a Logger in the service.
- Create a method in the service that logs "Hello World" with DEBUG level.
- Create a CommandLineRunner bean that runs the method.
- ? Did your log statement get printet?
- Set the logging.level to debug for the service's package in the configuration and run it again.

#### Solution
See Service at 101/logging/src/main/java/dk/lundogbendsen/logging/MyLoggingService.java
See CommandLineRunner for exercise1 at 101/logging/src/main/java/dk/lundogbendsen/logging/LoggingApplication.java

### Exercise 2: Simplify the logging pattern
- Set the logging.pattern for the console to just print the following pattern: <DATETIME> [<LEVEL>] <MESSAGE>
  
  Example: `2020-12-16 07:21:48.442 [DEBUG] Hello World`

- !Hint: date field can be formatted like `%d{yyyy-MM-dd HH:mm:ss.SSS}`
- !Hint: Logging level can be referenced using this code as `%5p`
- !Hint: The logging message uses the following code: `%m`
- !Hint: Add a LINE BREAK using this code: `%n`

#### Solution
`logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%5p] %m%n`

### Exercise 3: MDC - Pass variable across methods
- Add another method in the service from Exercise 1 that pulls out the userId from the MDC. 
- Log a message that includes the userId. 
- Make a new CommandLineRunner bean that runs the method.
- ? What is the value printet? 
  
- Modify the CommandLineRunner to set a userId in the MDC
- Execute the CommandLineRunner again.
- ? Does the userId appear in the logs?

#### Solution
See Service at 101/logging/src/main/java/dk/lundogbendsen/logging/MyLoggingService.java
See CommandLineRunner for exercise3 at 101/logging/src/main/java/dk/lundogbendsen/logging/LoggingApplication.java

### Exercise 4: MDC - Tracing - add MDC to logging pattern
So the MDC is good for methods to pass around data without explicitly putting it into the method signature. 

But the MDC is meant for something else. It's a logging framework concept after all. It is meant for automatically printing it to the logs. 

- Make a new method in the service that prints a log statement.
- Update the logging.pattern for the console to print the userId from the MDC.
- !Hint: a Key from the MDC can be referenced like this: `%X{Key}`.
- Make a new CommandLineRunner that sets a userId in the MDC runs the new method. 
- Execute the CommandLineRunner.
- ? Did the userId appear as expected?
- Update the CommandLineRunner and add an orderId to the MDC (with some random value).
- Execute the CommandLineRunner.
- ? Did the orderId appear?
- Update the logging pattern to print ALL keys in the MDC. 
- !Hint: To print all keys in MDC just use the `%X` code without specifying a key. 
- Execute the CommandLineRunner.
- ? Which keys did appear?

#### Solution
- Pattern for printing userId: `logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%5p] [%X{userId}] %m%n`.
- Pattern for printing all keys: `logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%5p] [%X] %m%n`.

### Exercise 5: MDC - Tracing - filters and intercepters
In a Web application, the MDC is often used to enrich log statements with _tracing_ information. Everything that happens during processing of a request can then be filtered out and inspected.

Therefore a TracingFilter is setup to automatically add a TraceId on behalf of the webapplication. 

- Add the following dependency to the `pom.xml` to get WEB support:
  
  (See `101/logging/pom.xml`)
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```  
- Create a _ServletFilter_ for automatically inserting a TraceId in the MDC:
  
  (See `101/logging/src/main/java/dk/lundogbendsen/logging/TracingFilter.java`) 
```
public class TracingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MDC.put("TraceId", UUID.randomUUID().toString());
        filterChain.doFilter(servletRequest,servletResponse);
        MDC.remove("TraceId");
    }
}
```
- Register the filter in Spring MVC:
  
  (See `101/logging/src/main/java/dk/lundogbendsen/logging/LoggingApplication.java`) 
```
@Bean
public FilterRegistrationBean filterRegistrationBean() {
    FilterRegistrationBean bean = new FilterRegistrationBean(new TracingFilter());
    return bean;
}
```
- Make an API:
  
  (See `101/logging/src/main/java/dk/lundogbendsen/logging/Api.java`)
```
@RestController
public class Api {
    Logger logger = LoggerFactory.getLogger(Api.class);
    @Autowired
    private MyLoggingService myLoggingService;            

    @RequestMapping("/api")
    public String api() {
        logger.info("ENTER api()");
        myLoggingService.myMethod();
        logger.info("EXIT api()");
        return MDC.get("TraceId");
    }
}
```

- Run the application - it is now a Web Application.

- Hit the http://localhost:8080/api

- !Hint: if you do not see the TraceId in the logs then check your loggin.pattern...

### Exercise 6: Logback configuration
Springboot gives you a lot of sensible defaults and configuration options. But if you want to get close and have more control, then you can inject a logback configuration. If a logback configuration file is present, it takes precedence over any Springboot configuration.

In this exercise you'll take control by creating a file called `resources/logback-spring.xml` with the following content:

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="Console"
              class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>
                %black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable
            </Pattern>
        </layout>
    </appender>

    <!-- LOG everything at INFO level -->
    <root level="info">
        <appender-ref ref="Console" />
    </root>

    <springProfile name="!production">
        <!-- configuration to be enabled when the "production" profile is not active -->
        <logger name="dk" level="trace" additivity="false">
            <appender-ref ref="Console" />
        </logger>
    </springProfile>

</configuration>
```

- Restart the application
- Hit http://localhost:8080/api
- ?Inspect the output - why is there no TraceId?
- Add TraceId to the logging pattern, restart API and inspect.

### Exercise 7: Logback extensions - use Json encoding
You may want to take control if you for example want to change the output to JSON to be compliant with the centralized logging system, your company is using.

In this exercise you'll update the logback configuration to change the console appender to output json. We are going to use the `LoggingEventCompositeJsonEncoder` to format the log statements into Json. 

The LoggingEventCompositeJsonEncoder can be configured to output a lot of information from the application like method, line, class and filename of the caller.

It also knows how to format a StackTrace nicely in JSON, so the centralized logging system can display it properly.

It is also able to read the application.properties to extract information from that.

- Add some dependencies for logback to be able to output logs as json: 

  (see `101/logging/pom.xml`)
```
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>6.5</version>
</dependency>
```
- The update the `logback-spring.xml` to this: 

  (see `101/logging/src/main/resources/logback-spring-jsonencoder.xml`)
```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property resource="application.properties" />
    <contextName>${spring.application.name}</contextName>
    <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <contextName>
                    <fieldName>app</fieldName>
                </contextName>
                <timestamp>
                    <fieldName>ts</fieldName>
                    <timeZone>UTC</timeZone>
                </timestamp>
                <loggerName>
                    <fieldName>logger</fieldName>
                </loggerName>
                <logLevel>
                    <fieldName>level</fieldName>
                </logLevel>
                <callerData>
                    <classFieldName>class</classFieldName>
                    <methodFieldName>method</methodFieldName>
                    <lineFieldName>line</lineFieldName>
                    <fileFieldName>file</fileFieldName>
                </callerData>
                <threadName>
                    <fieldName>thread</fieldName>
                </threadName>
                <mdc/>
                <arguments>
                    <includeNonStructuredArguments>false</includeNonStructuredArguments>
                </arguments>
                <stackTrace>
                    <fieldName>stack</fieldName>
                </stackTrace>
                <message>
                    <fieldName>msg</fieldName>
                </message>
            </providers>
        </encoder>
    </appender>

    <!-- LOG everything at INFO level -->
    <root level="info">
        <appender-ref ref="Console"/>
    </root>

</configuration>
```
- Restart the application.
- ?What happened to the output?

### Exercise 8: Logback - Use Spring profiles to set loglevel
The output from the previous exercise is json, which is very nice for a centralised logging system, but unreadable for the human eye.

In this exercise we will take advantage of a Springboot logback extension that allows us to use Spring profiles in the logging configuration.

We want to have "normal" output when running localhost. And we want to have json when running in an environment with centralised logging system.

We can use the springProfile snippet to help us.

- Add the following snippet to the logback-spring.xml:
```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- Reference to the Springboot configuration   -->
    <property resource="application.properties"/>
    <!-- Pull out the application name from the Springboot configuration -->
    <contextName>${spring.application.name}</contextName>
    <springProfile name="!default">
        <appender name="Console"
                  class="ch.qos.logback.core.ConsoleAppender">
            <!-- Use a Json encoder-->
            <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
                <providers>
                    <contextName>
                        <fieldName>app</fieldName>
                    </contextName>
                    <timestamp>
                        <fieldName>ts</fieldName>
                        <timeZone>UTC</timeZone>
                    </timestamp>
                    <loggerName>
                        <fieldName>logger</fieldName>
                    </loggerName>
                    <logLevel>
                        <fieldName>level</fieldName>
                    </logLevel>
                    <callerData>
                        <classFieldName>class</classFieldName>
                        <methodFieldName>method</methodFieldName>
                        <lineFieldName>line</lineFieldName>
                        <fileFieldName>file</fileFieldName>
                    </callerData>
                    <threadName>
                        <fieldName>thread</fieldName>
                    </threadName>
                    <mdc/>
                    <arguments>
                        <includeNonStructuredArguments>false</includeNonStructuredArguments>
                    </arguments>
                    <stackTrace>
                        <fieldName>stack</fieldName>
                    </stackTrace>
                    <message>
                        <fieldName>msg</fieldName>
                    </message>
                </providers>
            </encoder>
        </appender>
        <!-- LOG everything at INFO level -->
        <root level="info">
            <appender-ref ref="Console"/>
        </root>
    </springProfile>

    <springProfile name="default">
        <!-- configuration to be enabled when the "production" profile is not active -->
        <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
            <layout class="ch.qos.logback.classic.PatternLayout">
                <Pattern>
                    %black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable
                </Pattern>
            </layout>
        </appender>
        <logger name="dk" level="info" additivity="false">
            <appender-ref ref="Console"/>
        </logger>
    </springProfile>

</configuration>
```


### Exercise 9: ELK - Centralized logging using ELK
Now we have readable output for our local development, and we have prepared the output to be easily ingestable for a centralized logging system.

Let's setup a centralized Logging System using docker.

- Run `docker-compose -f 101/logging/elk/docker-compose.yml up`. 
  This is going to take 3-5 minutes.
  
- Then update the logback-spring.xml to the following 
  
  (see `101/logging/src/main/resources/logback-spring-elk.xml`)
```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- Reference to the Springboot configuration   -->
    <property resource="application.properties"/>
    <!-- Pull out the application name from the Springboot configuration -->
    <contextName>${spring.application.name}</contextName>
    <springProfile name="!default">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>SpringBoot-ex08-Logging/logs/ex08.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>elk/logs/myapp-%d{yyyy-MM-dd}.log</fileNamePattern>
                <maxHistory>7</maxHistory>
            </rollingPolicy>
            <!-- Use a Json encoder-->
            <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
                <providers>
                    <contextName>
                        <fieldName>app</fieldName>
                    </contextName>
                    <timestamp>
                        <fieldName>ts</fieldName>
                        <timeZone>UTC</timeZone>
                    </timestamp>
                    <loggerName>
                        <fieldName>logger</fieldName>
                    </loggerName>
                    <logLevel>
                        <fieldName>level</fieldName>
                    </logLevel>
                    <callerData>
                        <classFieldName>class</classFieldName>
                        <methodFieldName>method</methodFieldName>
                        <lineFieldName>line</lineFieldName>
                        <fileFieldName>file</fileFieldName>
                    </callerData>
                    <threadName>
                        <fieldName>thread</fieldName>
                    </threadName>
                    <mdc/>
                    <arguments>
                        <includeNonStructuredArguments>false</includeNonStructuredArguments>
                    </arguments>
                    <stackTrace>
                        <fieldName>stack</fieldName>
                    </stackTrace>
                    <message>
                        <fieldName>msg</fieldName>
                    </message>
                </providers>
            </encoder>
        </appender>
        <!-- LOG everything at INFO level -->
        <root level="info">
            <appender-ref ref="FILE"/>
        </root>
    </springProfile>

    <springProfile name="default">
        <!-- configuration to be enabled when the "production" profile is not active -->
        <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
            <layout class="ch.qos.logback.classic.PatternLayout">
                <Pattern>
                    %black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable
                </Pattern>
            </layout>
        </appender>
        <logger name="dk" level="info" additivity="false">
            <appender-ref ref="Console"/>
        </logger>
    </springProfile>

</configuration>
```
- Run the application. This should produce a file `101/logging/elk/logs/myapp.log`. The Logstash container in the ELK stack is setup to ingest this file and index it into ElasticSearch. Unfortunately in this setup Logstash is unable to detect file changes made by the FileAppender, so we have to stop the application to see ingestion.
- Goto http://localhost:5601 to start Kibana.
- Goto http://localhost:5601/app/management/kibana/indexPatterns/create to make an index for your application.
- use the pattern `logback-*` and press "Next Step".
- Select the `ts` field for timestamp and press Create index pattern.
- Goto http://localhost:5601/app/discover#/ to explore the index.

### Exercise 10: Distributed Tracing - setup filters in Kibana
Every request to the http://localhost/api is now stamped with a TraceId. This makes it possible to extract all the log lines related to a specific request to see what happened. Maybe an error occurred, and you are looking into it. In this way you can find out what happened to the user.

All logs are now sent to ElasticSearch for indexing. In this exercise we will use Kibana to help us out. 

- Goto http://localhost:5601/app/discover#
- To the left there is a section with _selected fields_ and _available fields_. Make sure to select the following fields: `msg, orderId, userId, TraceId`
- The main window now updates to display values for the selected fields. Holding the mouse over a field reveals two icons for filtering on this exact value. Filter on one of the TraceIds.


### Exercise 11: Sleuth - Distributed Tracing
The Distributed Trace Challenge just grows when you get more services. For each service you need gatekeepers when requests arrive and when when it is passed further down stream to another service.

You can think of a service as having many channels IN that starts a process, but also many channels OUT the initiates work in other services. Each of these channels must be guarded like we did with the TracingFilter. For incoming channels, we must copy the TraceId if present, or we must create a new one. For outgoing channels, we must add the TraceId as part of the data we send, so the next service can benefit. 

This means a lot of repetitive work in every service. Springboot has a solution for this: The Sleuth project.

In this exercise we will replace our own Tracing solution with a much better one.

- Add this dependency-management for Springboot cloud features:
```
	<repositories>
		<repository>
			<id>spring-milestones</id>
			<name>Spring Milestones</name>
			<url>https://repo.spring.io/milestone</url>
		</repository>
	</repositories>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>2020.0.0-RC1</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
```
- The add the Sleuth dependency:
```
<dependency>
 <groupId>org.springframework.cloud</groupId>
 <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
```
- Then restart your application. Hit http://localhost/api a couple of times.
- Stop the application to make Logstash detect a file change.
- Go to Kibana and refresh the fields in the `logback-*` index: http://localhost:5601/app/management/kibana/indexPatterns
- Goto http://localhost:5601/app/discover# and see the Sleuth fields `spanId` and `traceId`.
