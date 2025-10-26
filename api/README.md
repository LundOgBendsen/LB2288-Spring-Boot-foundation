# API

## Exercises - Section 1: Controlling input and output

### Exercise 1: Create a RestController
- Create a Springboot Web project
- Create a class MyRestController
- Annotate the class with @RestController. This instructs Spring to register the class as a request handler.
- Create a method `public String helloWorld()` returning a String with content "HelloWorld".
- Annotate the method with @GetMapping. This instructs Spring that this controller handles GET requests without a path and without data.
- Run the application.
- Go to http://localhost:8080

#### Solution
```java
@RestController
public class MyRestController {
    @GetMapping("/")
    public String helloWorld() {
        return "Hello World";
    }
}
```

### Exercise 2: Input to controller
You can map a method parameter to either a query parameter or a form field using the @RequestParam annotation.
- Modify the helloWorld() method by adding a String message as parameter.
- Modify the hard coded return value to `"Hello world: " + message`.
- The new parameter must be annotated with @RequestParam. This instructs Spring to map a query parameter from the request to this parameter. The query parameter must be called "message".
- Restart the application.
- Go to http://localhost:8080?message=alive!

#### Solution
```java
@RestController
public class MyRestController {
    @GetMapping("/")
    public String helloWorld(@RequestParam String message) {
        return "Hello World: " + message;
    }
}
```

### Exercise 3: Input to controller by path
You can map a method parameter to some part of the request path using the @PathVariable annotation.
- Make a method `public Long pathMapping(Long id)`.
- Let it return the id;
- Annotate the method with `@GetMapping("{id}")`.
- Annotate the id parameter with `@PathVariable`.
- Restart the application.
- Go to http://localhost:8080/666

#### Solution
```java
@GetMapping("/{id}")
public Long pathMapping(@PathVariable Long id) {
    return id;
}
```

### Exercise 4: Map input to POJO
You can have Springboot convert the request body to a Pojo. For instance is the client is sending Json, you could make a Model representing the Json and Springboot will make a Pojo with the input.
- Make a class Person with fields firstName and lastName. Add setters and getters (Hint: Lombok).
- Make a method `public void receivePerson(Person person)` in the RestController from exercise 2.
- Annotate the method with @PostMapping to instruct Spring to map the POST verb.
- Annotate the person parameter with @RequestBody to instruct Spring to convert the request body to a Person object
- Restart the app.
- Run `curl -H "content-type: application/json" localhost:8080 -d '{"firstName":"Christian","lastName":"Jensen"}'`.

#### Solution
```java
@PostMapping("/")
public void receivePerson(@RequestBody Person person) {
    System.out.println("person = " + person);
}
```

### Exercise 5: Upload file
A file upload is really just a http request where the content part should be interpretated as a file. But we have to receive it as a MultipartFile object. Let's try to receive an upload:
- Make a method `public String receiveFile(@RequestParam("file") MultipartFile file)`
- Annotate the method with @PostMapping("/upload-file") to instruct Spring to map the method to the POST verb and the path /upload-file.
- In the method take out the bytes from the file, convert them to a new String(), print it and return the filename.
- Restart the application
- Run `curl localhost:8080/upload-file -F "file=@pom.xml"` (or some other local text-file)

#### Solution
```java
@PostMapping("/upload-file")
public String receiveFile(@RequestParam("file") MultipartFile file) throws IOException {
    final String content = new String(file.getBytes());
    System.out.println("content = " + content);
    return file.getOriginalFilename();
}
```


### Exercise 6: Get Header
You can map any header to your method by using the @RequestHeader annotation.
- Modify the receiveFile method by adding the parameter `@RequestHeader(name = "Content-Type") String contentType`.
- Print out the content type to the console.
- Restart the application.
- Run `curl localhost:8080/upload-file -F "file=@pom.xml"` (or some other local text-file)

#### Solution
```java
@PostMapping("/upload-file")
public String receiveFile(@RequestParam("file") MultipartFile file, @RequestHeader(name = "Content-Type") String contentType) throws IOException {
    final String content = new String(file.getBytes());
    System.out.println("content = " + content);
    System.out.println("contentType = " + contentType);
    return file.getOriginalFilename();
}
```

### Exercise 7: Pojo output
It is easy to send Strings and primitives like longs and ints. Just let the controller method return them, and they are automatically converted to the response body.
But it is just as easy to send a complex object like a pojo.
- Make a method `public Person getPerson()` that creates a Person object and returns it.
- Annotate the method with `@GetMapping("person")`
- Restart the application.
- Run `curl localhost:8080/person`.

#### Solution:
```java
@GetMapping("person")
public Person getPerson() {
    Person p = new Person();
    p.setFirstName("Christian");
    p.setLastName("Jensen");
    return p;
}
```

### Exercise 8: Complete control using ResponseEntity
Sometimes you want to control the response headers in the Controller. In this case you can make the method return a ResponseEntity. 
The ResponseEntity represents both http status code, headers and content in one object.

- Make a method `public ResponseEntity<Person> getPersonSpecial()`
- Annotate with `@GetMapping("person-special")`
- Make a Person object.
- Make a new ResponseEntity object for a Person object.
- Add a header "TraceId" with some random value. A practical application for this TraceId would be for the client to have a concrete reference to the the request in case something goes wrong and the client wants to call customer support.
- Add a status code "OK".
- Add the person object.
- Return the ResponseEntity.
- Restart the application.
- Run `curl localhost:8080/person-special -i` and inspect the result.

#### Solution
```java
@GetMapping("person-special")
public ResponseEntity<Person> getPersonSpecial() {
    final Person person = new Person();
    person.setFirstName("Christian");
    person.setLastName("Jensen");
    HttpHeaders headers = new HttpHeaders();
    headers.add("TraceId", "abc123");
    final ResponseEntity<Person> ok = new ResponseEntity<>(person, headers, HttpStatus.OK);
    return ok;
}
```

### Exercise 9: Download a File
You can use the ResponseEntity to serve files to the client.
- Make a method `public ResponseEntity<Resource> download(String param) throws IOException`.
- Annotate with `@GetMapping(path = "/download")`
- Create a File object pointing to an actual file on your harddrive.
- Create an InputStreamResource to wrap the file like: `InputStreamResource resource = new InputStreamResource(new FileInputStream(file))`.
- Create a ResponseEntity of type InputStreamResource where you set:
  - content-length (length of file)
  - content-type (MediaType.APPLICATION_OCTET_STREAM)
  - content-disposition (attachment; filename=<the files name>)
  - body (the InputStreamResource you created)
- Restart the application.
- Go to localhost:8080/download in a browser

#### Solution
```java
  @GetMapping("/download")
  public ResponseEntity<Resource> download() throws IOException {
      File file = new File("api/http-status-codes.png");
      InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

      return ResponseEntity.ok()
              .contentLength(file.length())
              .contentType(MediaType.APPLICATION_OCTET_STREAM)
              .header("Content-Disposition", "attachment; filename=http-status-codes.png")
              .body(resource);
  }
```

### Exercise 10: Client wants XML
In http there is a concept of Content Negotiation. The client sends a header "Accept" which declares which formats it can consume. 
The DispatcherServlet will then try to convert the result of the controller to the accepted format.
- In order to serve XML, a new dependency must be added to the pom.xml:
```xml
<dependency>
  <groupId>com.fasterxml.jackson.dataformat</groupId>
  <artifactId>jackson-dataformat-xml</artifactId>
</dependency>
```
- Restart the Application.
- Run `curl localhost:8080/person -H "Accept: application/xml"`.


### Exercise 11: Client sends XML
Json is the dominating paradigm of message exchange format on the web. But sometimes the client send XML.
Since you have added the XML capable converter as a dependency in the previous exercise, you can now receive XML as well: 
- Run `curl -H "content-type: application/xml" localhost:8080 -d '<Person><firstName>Christian</firstName><lastName>Jensen</lastName></Person>'`







## Exercises - Section 2: Exception Handling
When exceptions happens, Springboot gives you some basic handling out of the box. 

If errors are client side, it has a variety of handlers that produce meaningful error reponses for that. All other errors result in a 500 error.

As always Springboot lets you take complete control by adding up some ExceptionHandlers. You can control exceptions directly in the controller using the @ExceptionHandler annotation, or you can create a @RestControllerAdvice that handles exceptions across many controllers.

The latter approach is recommended.

Use the project *springbootcourse/api/person-service-api* as a starting point for the following exercises.

### Exercise 0: make springbootcourse/api/person-service-api project work
The api project requires a PersonService which is currently not on the classpath.

But you made a person-service-starter in one of the previous exercises, and now it is time to use it.

Update the pom.xml to include the person-starter-demo as a dependency.


The `api/person-service-api/src/main/java/dk/lundogbendsen/restassignmentapi/controllers/PersonController.java` is a controller that uses a PersonService to create, update and delete persons.
But right now all the code inside is commented out. You must uncomment the code to have a fully functioning PersonController to work with in the following exercises.


#### Solution

```xml
        <dependency>
            <groupId>org.example</groupId>
            <artifactId>person-service</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.example</groupId>
            <artifactId>person-service-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

```

### Exercise 1: ExceptionHandlers in Controllers
The PersonService throws a PersonCreateException in the create method if the Person object already contains an id. 
In this exercise you must handle that exception and provide a proper http status code and message.

Improve the API you made for PersonService to handle this situation. Right now it will just serve a standard White label error. We want to show a nice error message with status code 400 and a descriptive text.

- In the API you made for the PersonService make a method that takes a PersonCreateException as parameter.
- It must return a String.
- It must be annotated with ResponseStatus returning a 400 status code.
- It must be annotated with @ExceptionHandler
- Restart the application.
- In Postman create a person where you add an ID to the body of the request.


#### Solution
```java
@ExceptionHandler
@ResponseStatus(HttpStatus.BAD_REQUEST)
public String personCreateExceptionHandler(PersonCreateException e) {
    return e.getMessage();
}
```

```text
curl 'localhost:8080' -d '{"name": "Jonathan","age": 16,"id": 1}' -H "Content-Type: application/json"
```


### Exercise 2: ExceptionHandlers in ControllerAdvices
In this exercise we will make a class for handling exceptions.

- Make a new class PersonApiControllerAdvice.
- Annotate it with @RestControllerAdvice
- Define a method public String handleServiceExceptions(Exception e) 
- Annotate with @ExceptionHandler that handles all 3 Exceptions in the `dk.lundogbendsen.springbootcourse.api.personservice.service.exceptions` package
- Delete the ExceptionHandler made in exercise 1.
- Restart the application.
- In Postman create a person where you add an ID to the body of the request.

#### Solution
```java
@RestControllerAdvice
public class PersonApiControllerAdvice {
    @ExceptionHandler({PersonCreateException.class, PersonUpdateException.class, PersonNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(Exception e) {
        return e.getMessage();
    }
}
```

### Exercise 3: Exceptions extending ResponseStatusException
In this exercise you'll handle the create exception directly in the api controller by catching it in a try/catch and throw a new exception extinding the ResponseStatusException.

- Make a copy of the create() method and call it create2(). Also update the PostMapping to map /create2.
- The new method must catch the expected PersonCreateException and instead throw a new ResponseStatusException:
    - Status code = 404
    - Message = Person not Found.
    - exception included.
- Restart the application.
- In Postman create a person where you add an ID to the body of the request and call the new method (create2).

#### Solution
```java
@PostMapping("create2")
@ResponseStatus(HttpStatus.CREATED)
public Person create2(@RequestBody Person person) {
    final Person created;
    try {
        created = personService.create(person);
    } catch (PersonCreateException e) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found", e);
    }
    return created;
}
```

### Exercise 4: Make your own ErrorController
Sometimes you need special handling of uncaught exceptions in your application. It could be that you want to notify Operations immediately about it.

You can take over from the BasicErrorController by making your own Controller and map it to /error.

If you do so, the Autoconfiguration will back off and not setup the BasicErrorController.

- Make a new class, MyErrorController, and annotate it `@RestController` and `@RequestMapping("/error")`.
- Implement the `ErrorController` interface.
- Make a method `public ResponseEntity<Map<String, Object>> myErrorHandler(ServletWebRequest webRequest)`.

The ServletWebRequest webRequest is a handle to the new request created by the Webserver when the DispatcherServlet has given up and asks for the error page. 
It includes a reference to the original request, where the DispatcherServlet has put a lot of attributes regarding the Exception, including the StackTrace.

- Pull out the exception attributes from the request with this snippet:
```java
final DefaultErrorAttributes defaultErrorAttributes = new DefaultErrorAttributes();
final Map<String, Object> errorAttributes = defaultErrorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE));
```
- Print out the errorAttributes. This is where you could notify your operations about details of the uncaught exception or maybe just enrich with a TraceId from the request for further investigation.
- Return a Map with the message from the "error" key in the errorAttributes. This is where the BasicErrorController normally would create its WhiteLabel Error.

#### Solution
```java
@RestController
@RequestMapping("/error")
public class MyErrorController implements ErrorController {
  @RequestMapping
  public ResponseEntity<Map<String, Object>> handleError(ServletWebRequest webRequest) {
    final DefaultErrorAttributes defaultErrorAttributes = new DefaultErrorAttributes();
    final Map<String, Object> errorAttributes = defaultErrorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE));
    System.out.println("errorAttributes = " + errorAttributes);
    Map<String, Object> body = Map.of("Message", errorAttributes.get("error"));
    return new ResponseEntity<>(body, HttpStatus.valueOf((Integer) errorAttributes.get("status")));
  }
}
```



## Exercises section 3: Filters and Intercepters

Use the project *springbootcourse/api/person-service-api* as a starting point for the following exercises.

### Exercise 1: Security Filter
Let's make the Person API a bit more secure by adding a Filter that checks if you are authenticated. 
Only requests with a header containing an apiToken can pass.

- Make a class SecurityFilter implementing the Filter interface.
- Implement the doFilter() method.
- In the doFilter method extract the http header "apiToken" (hint: cast the request parameter to a HttpServletRequest)
- If the apiToken is NOT present, send an error of 401 with a descriptive message and return (hint: cast the response to HttpServletResponse and use the sendError()).
- Otherwise call the filter chain.
- Register the new filter by creating a @Bean method that returns a FilterRegistrationBean with your filter.
- Restart the application.
- Run `curl localhost:8080/`

This should result in a whitelabelled 401 response.

- Run `curl localhost:8080/ -H "apiToken: abc"`

Now you should see the list of persons.

#### Solution
```java
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

public class SecurityFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        final String apiToken = req.getHeader("apiToken");
        if (apiToken == null) {
            System.out.println("Unauthorized");
            HttpServletResponse res = (HttpServletResponse) response;
            res.sendError(HttpStatus.UNAUTHORIZED.value(), "You shall not pass!");
            return;
        }

        chain.doFilter(request, response);
    }
}
```

```java
@Bean
public FilterRegistrationBean<SecurityFilter> filterRegistrationBean() {
    return new FilterRegistrationBean<>(new SecurityFilter());
}
```

### Exercise 2: Make security in an Intercepter
We will now make the same security check by using an Intercepter.

- Outcomment the @Bean that registers the Filter from exercise 1.
  
- Make a new UnauthorizedException class extending the ResponseStatusException that call super with `super(HttpStatus.UNAUTHORIZED, "The request requires an ApiToken")`.
- Make a new class SecurityIntercepter that implements HandlerInterceptor.
- Override the preHandle method.
- If there is no apiToken header, then throw a new UnauthorizedException.
- Otherwise return true.
- Make a new class IntercepterConfig that implements WebMvcConfigurer. The new class should be annotated @Component.
- Implement the addInterceptors() method and add your new SecurityIntercepter.
- Restart the application.
- Run `curl localhost:8080/`

This should result in a 401.

- Run `curl localhost:8080/ -H "apiToken: abc"`


#### Solution
```java
public class UnauthorizedException extends ResponseStatusException {
    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED, "The request requires an ApiToken");
    }
}
```

```java
public class SecurityIntercepter implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String apiToken = request.getHeader("apiToken");
        if (apiToken == null) {
            System.out.println("Unauthorized");
            throw new UnauthorizedException();
        }
        return true;
    }
}
```

```java
@Component
public class IntercepterConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityIntercepter());
    }
}
```

