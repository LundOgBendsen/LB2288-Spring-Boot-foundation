# Actuator

## Exercises on Application Metadata

Create a new Springboot project and add "Web" and "Actuator" starters.


### Exercise 1:  Enable ALL endpoints

Springboot offers a lot of endpoints to be exposed through the Actuator. A few examples 

**beans, configuration, env, mappings, info**

Make the project expose ALL available endpoints.

Run the application and hit http://localhost:8080/actuator

Hint: The configuration property controlling exposure of endpoints is `management.endpoints.web.exposure.include`.

#### Solution
In application.properties add this entry:
```
management.endpoints.web.exposure.include=*
```


### Exercise 2: Inspect the properties of your application

Springboot looks after Configuration Properties for your application in a lot of different places. Through the Actuator you are able to inspect what property got resolved to what value.

Add the following entry to application.properties:
```
server.port=8080
```

Run the application and find the property using one of the exposed endpoints in the Actuator API.

Hint: you are looking for Configuration Properties. The ID of the properties endpoint is `configprops`. 
You can find the list of ids in https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints
Hint: make sure all endpoints are exposed using the solution from exercise 1.


#### Solution:
http://localhost:8080/actuator/configprops

```
...
"server-org.springframework.boot.autoconfigure.web.ServerProperties": {
  "prefix": "server",
  "properties": {
    "port": 8080
...
```


### Exercise 3: Expose properties from your own Config classes in the Actuator

Make a **Configuration Property class** called _MailConfig_ with the following properties:

```
String smtpServer;
int port = 25;
String username;
String password;
```

Reload the application and find them in the

http://localhost:8080/actuator/configprops

Hint: A Configuration Property class must be annotated with `@Configuration` and `@ConfigurationProperties`.

#### Solution
see 101/actuator/src/main/java/dk/lundogbendsen/health/MailConfig.java



### Exercise 4: Add a META-INF/build-info.properties and see the build info in the exposed endpoint

- Use maven to generate the file by adding the below snippet to the `spring-boot-maven-plugin` in the pom.xml file:
```
<executions>
    <execution>
        <goals>
            <goal>build-info</goal>
        </goals>
    </execution>
</executions>
```
- Open a terminal and rebuild the project using `mvn clean package`.

#### Solution:

The plugin-snippet should look like this:
```
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>build-info</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Run the `mvn clean package` command.

Find the build info at http://localhost:8080/actuator/info


### Exercise 5: Add Team information to the info section

Add team information such as Team name, Contact email, and Team Lead to the info section.

Custom properties like these are available throguh the `info` endpoint if the 'env' property is enabled:

`management.info.env.enabled=true`

See the info exposed through the Actuator endpoint.

#### Solution
Add the following entries to the application.properties:
```
management.info.env.enabled=true
info.myname=christian
info.team.team-name=Team Alfa-Bravo
info.team.team-lead=The Boss
info.team.contact-email=team-alfa-bravo@example.com

```

Hit the http://localhost:8080/actuator/info


## Exercises on Health

### Exercise 6: Enable the RabbitMQ build-in health indicator

- Add the `spring-boot-starter-amqp` starter to the `pom.xml`.
- Enable the rabbit health check by adding `management.health.rabbit.enabled=true` to the `application.properties`
- Make info available: `management.endpoint.health.show-details=always`.
- Restart the application and hit the health endpoint to see system status.
- Hit: http://localhost:8080/actuator/health

What is the status of the overall health check? (DOWN)
 
 
- What can we do to make the health check report UP?
```commandline
docker-compose -f ../../docker-compose.yml up rabbit
```

NB! It might take 30 seconds for the health check to report UP.

### Exercise 7: Create your own health check: ServiceMaintenanceHealthIndicator

Create a Health Check that reports down if the service is in maintenance. 


- Create a new class called `ServiceWindowHealthIndicator` that implements `HealthIndicator`.
- Make it both a `@Component`.
- Add a boolean field `inMaintenance`.
- Add a method `setInMaintenance(boolean inMaintenance)` to toggle the maintenance mode.
- Implement the `health` method to return `Health.down()` if `inMaintenance` is true otherwise `Health.up()`.
- Hit the http://localhost:8080/actuator/health endpoint and see the status of the service.


#### Solution
```java
@Component
public class ServiceMaintenanceHealthIndicator implements HealthIndicator {

    @Setter
    private Boolean inMaintenance = false;

    public void setInMaintenance(Boolean inMaintenance) {
        this.inMaintenance = inMaintenance;
    }
    @Override
    public Health health() {
        if (!inMaintenance) {
            return Health.up().build();
        }
        return Health.down()
                .withDetail("Reason", "In Maintenance period")
                .build();
    }
}
```

### Exercise 8: Create a customer endpoint to toggle the maintenance mode of the healthcheck in the ServiceMaintenanceHealthIndicator
Now we will expose a REST endpoint to toggle the maintenance mode of the health check.

We make use of the @Endpoint and @WriteOperation annotations to create a custom endpoint.

- Create a new class called `ServiceMaintenanceEndpoint` and annotate it with `@Endpoint`.
- Wire in the `ServiceMaintenanceHealthIndicator`.
- Add a method `@WriteOperation` that receives a Boolean and uses that value to call the ServiceMaintenanceHealthIndicator's `setInMaintenance()` method.
- Restart the application and hit the http://localhost:8080/actuator/health endpoint and see the status of the service.

- Toggle the maintenance mode by hitting the endpoint with a POST request:

`curl http://localhost:8080/actuator/service-maintenance -H "Content-Type: application/json" -d '{"inMaintenance":"True"}'`

- Hit the health endpoint and see the status of the service.


#### Solution
```java
@Endpoint(id = "service-maintenance")
@Component
public class ServiceMaintenanceTogglerEndpoint {

    @Autowired
    private ServiceMaintenanceHealthIndicator serviceMaintenanceHealthIndicator;

    @WriteOperation
    public void setInMaintenance(Boolean inMaintenance) {
        serviceMaintenanceHealthIndicator.setInMaintenance(inMaintenance);
    }
}
```

## Springboot and Kubernetes
inspiration: https://bell-sw.com/blog/spring-boot-monitoring-in-kubernetes-with-prometheus-and-grafana/


### Exercise 9: Setup Prometheus and Grafana in Kubernetes
- add bitnami helm repo with `helm repo add bitnami https://charts.bitnami.com/bitnami`
- install prometheus with `helm install prometheus bitnami/kube-prometheus`
- install Grafana with `helm install grafana bitnami/grafana`
- get grafana password with `echo "Password: $(kubectl get secret grafana-admin --namespace default -o jsonpath="{.data.GF_SECURITY_ADMIN_PASSWORD}" | base64 -d)"`
- expose prometheus with `kubectl port-forward --namespace default svc/prometheus-kube-prometheus-prometheus 9090`
- expose grafana with `kubectl port-forward --namespace default svc/grafana 3000`
- login to grafana with admin and the password from the echo command
- add a new data source with the url `http://prometheus-kube-prometheus-prometheus.default.svc.cluster.local:9090`
- add a new dashboard with the id `12900` and the name `Spring Boot Actuator`

### Exercise 10: Finish person-service-api:
- add `spring-boot-starter-actuator` and `io.micrometer:micrometer-registry-prometheus` to the pom.xml
- add `management.endpoints.web.exposure.include=health,metrics,prometheus` to application.properties
- copy the Dockerfile from `api/person-service-api/Dockerfile` to the root of the project
- build a docker image with `docker build . -t person-service-api`
- copy the kubernetes deployment from `api/person-service-api/kubernetes/deployment.yaml` to the root of the project
- start minukube with `minikube start`
- load the docker image with `minikube image load person-service-api`
- apply the kubernetes manifest with `kubectl apply -f deployment.yaml`
- expose the service with `kubectl port-forward --namespace default svc/spring-boot-app-service 8080`
- check localhost:8080/person
- check localhost:8080/actuator/health
- check localhost:8080/actuator/metrics
- check localhost:8080/actuator/prometheus

### Exercise 11: Check the dashboards in Grafana
- http://localhost:3000/d/X034JGT7Gz/springboot-apm-dashboard?orgId=1&from=now-5m&to=now&refresh=auto
