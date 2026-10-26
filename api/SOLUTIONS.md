# API Solutions

Solutions for the exercises in [README.md](README.md).

## Section 1: Controlling input and output

### Exercise 1: Create a RestController

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

```java
@GetMapping("/{id}")
public Long pathMapping(@PathVariable Long id) {
    return id;
}
```

### Exercise 4: Map input to POJO

```java
@PostMapping("/")
public void receivePerson(@RequestBody Person person) {
    System.out.println("person = " + person);
}
```

### Exercise 5: Upload file

```java
@PostMapping("/upload-file")
public String receiveFile(@RequestParam("file") MultipartFile file) throws IOException {
    final String content = new String(file.getBytes());
    System.out.println("content = " + content);
    return file.getOriginalFilename();
}
```

### Exercise 6: Get Header

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

## Section 2: Exception Handling

### Exercise 0: make springbootcourse/api/person-service-api project work

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

```java
@ExceptionHandler
@ResponseStatus(HttpStatus.BAD_REQUEST)
public String personCreateExceptionHandler(PersonCreateException e) {
    return e.getMessage();
}
```

```text
curl 'localhost:8080' -X POST -d '{"name": "Jonathan","age": 16,"id": 1}' -H "Content-Type: application/json"
```

### Exercise 2: ExceptionHandlers in ControllerAdvices

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

```java
@PostMapping("create2")
@ResponseStatus(HttpStatus.CREATED)
public Person create2(@RequestBody Person person) {
    final Person created;
    try {
        created = personService.create(person);
    } catch (PersonCreateException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed request body", e);
    }
    return created;
}
```

### Exercise 4: Make your own ErrorController

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

## Section 3: Filters and Interceptors

### Exercise 1: Security Filter

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

### Exercise 2: Make security in an Interceptor

```java
public class UnauthorizedException extends ResponseStatusException {
    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED, "The request requires an ApiToken");
    }
}
```

```java
public class SecurityInterceptor implements HandlerInterceptor {
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
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityIntercepter());
    }
}
```