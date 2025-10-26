# Scheduled

## Exercises

### Exercise 1: Write a CRON expression that triggers every 10 minutes during working hours
Working hours are set to 9 - 17, monday to friday.

#### Solution
```
0 */10 9-17 * * MON-FRI
```

### Exercise 2: Wake a process every minute in the first and last hour of the business day

#### Solution
```
0 * 9-10,16-17 * * MON-FRI
```

### Exercise 3: Make a service run every 10 second.
- Make a Springboot project called "scheduled" with the Redis starter.
- Make a service that print prints a statement every 10 second.
- Enable scheduling.
- Run the application.

#### Solution
```java
@SpringBootApplication
@EnableScheduling
public class ScheduledApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduledApplication.class, args);
    }
    @Bean
    public ScheduledService scheduledService() {
        System.out.println("Preparing light Job Bean");
        return new ScheduledService();
    }

}
```

````java
public class ScheduledService {
    @Scheduled(cron = "*/10 * * * * *")
    public void myCronScheduledMethod() {
        System.out.println("CronTime is " + new Date());
    }
}
````


### Exercise 4: Scheduling in a multiple instances environment
In normal runtime scenarios you will have multiple instances of your service running.
Each instance is a copy of the application including the scheduled annotations, and they will wake up the exact same time.

In this exercise we will make your service run with multiple instances. We are going to use Docker for that.

- create a file `docker-compose.yml` in the root of your new project with the following content:
```yaml
version: "3"
services:
  redis:
    image: redis
    ports:
    - 6379:6379

  service:
    build:
      context: .
    deploy:
      replicas: 4
    environment:
      - SPRING_REDIS_HOST=redis
```
- create a file `Dockerfile` in the root of your project with the following content: 
```dockerfile
FROM openjdk:15-jdk-alpine
COPY target/scheduled-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```
- Build you project with maven using the command `mvn package`.
- Run the command `docker-compose build`. This should build a Docker image with your project.
- Run the command `docker-compose up`.

Docker will now spin up 4 instances of your service. Each instance will print its output to the console with its own colour.
You should soon see the services beginning to print the current datetime exactly at the same time.

#### Solution
See project `101/scheduled`


### Exercise 5: Coordinating jobs between multiple instances
All services printing the time is probably not what you intended. You want the job done one time - not a random amount of times.

Therefore you need to coordinate which instance that "has it". The instances need a protocol for competing for a token, and the winner does the job.
This means a kind of shared space, where access is controlled by the shared space.

There are many ways of doing this. One way could be a database where you take a lock on a table-row. If you get the lock, you do the job. Otherwise you back off.

In this exercise you will spin up a Redis server for coordination between instances.
Redis is a memory-database running single-threaded and is a very good choice for handling a distributed lock.

In this exercise we will modify the service that prints the time to grap for a distributed lock before doing any work.

- In the service Autowire in the RedisTemplate that comes from the Redis starter.
- Make a new method for grapping the lock from Redis. An example could be the following: 
```java
    private boolean takeDistributedLock() {
        final Boolean transfer_lock = redisTemplate.opsForValue().setIfAbsent("TRANSFER_LOCK", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), 5, TimeUnit.SECONDS);
        return transfer_lock;
    }
```
- Update the scheduled service to only print if a lock was successfully acquired.

Now it is time the rebuild you project.
- Exit the docker-compose process (ctrl-C)
- Run the command `mvn package` to build a new jar file with your code.
- Run the command `docker-compose build`. 
- Run the command `docker-compose up`.

With any luck you will now see only one service at a time printing the time.
