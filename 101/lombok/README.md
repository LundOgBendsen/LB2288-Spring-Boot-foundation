# Lombok

## Exercies

### Exercise 1: Use @SneakyThrows to avoid clumpsy handling of checked exceptions

Create a class with this utility method:

```
public String utf8ToString(byte[] bytes) throws UnsupportedEncodingException {
    return new String(bytes, "UTF-8");
}
```

It throws a checked exception which is unnecessary because UTF-8 is always legal. Use @SneakyThrows to remove the checked exception from the method signature. 

Hint: Look up usage of @SneakyThrows in the java doc.


#### Solution
```
public class StringUtility {
    @SneakyThrows(UnsupportedEncodingException.class)
    public String utf8ToString(byte[] bytes) {
        return new String(bytes, "UTF-8");
    }
}
```



### Exercise 2: Use @Cleanup to auto close resources

Java have the auto close feature meant to handle this situation

```
static String readFirstLineFromFileWithFinallyBlock(String path)
                                                     throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(path));
    try {
        return br.readLine();
    } finally {
        if (br != null) br.close();
    }
}
```

Problem is that it is clumpsy and error prone. You have to remember the finally block. Java 7 introduced Closables to convert this into:

```
static String readFirstLineFromFile(String path) throws IOException {
    try (BufferedReader br =
                   new BufferedReader(new FileReader(path))) {
        return br.readLine();
    }
}
```

This gives better readablity - but it is unintuitive and still looks clumpsy.

Use @Cleanup to implement a beautiful clean solution to solve the same problem.

Hint: Look up usage of @Cleanup in the java doc.



#### Solution
```
    @SneakyThrows
    static String readFirstLineFromFile(String path) {
        @Cleanup BufferedReader br = new BufferedReader(new FileReader(path));
        return br.readLine();
    }
```



### Exercise 3: Use @Slf4j to inject a Logger into a Service
Normally every service needs to log stuff. They therefore create a Logger like this:

```
@Service
public class ImportantService {
    Logger logger = LoggerFactory.getLogger(CustomerService.class);
    public void myMethod() {
        logger.info("Something important just happened...");
    }
}        
```

Use Lomboks @Slf4j annotation to inject a Logger instead of manually creating one on every service.


#### Solution
```
@Service
@Slf4j
public class ImportantService {
    public void myMethod() {
        log.info("Something important just happened...");
    }
}        
```


### Exercise 4: Make the perfect Pojo with Getters, Setters, ToString, HashCode and Equals implementations

Normally you are responsible for besides declaring fields of a Pojo to also implements getters and setters and hashCode and Equals methods. You also need to override the toString() method in order to easily log the content of the pojo runtime. Below is an example:

```
public class Customer {
    private String name;
    private Long id;
    private String email;
    private Date created;

    public static Customer.CustomerBuilder builder() {
        return new Customer.CustomerBuilder();
    }

    public String getName() {
        return this.name;
    }

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Customer)) {
            return false;
        } else {
            Customer other = (Customer)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59: {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null) {
                        if (other$id == null) {
                            break label59;
                        }
                    } else if (this$id.equals(other$id)) {
                        break label59;
                    }

                    return false;
                }

                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name != null) {
                        return false;
                    }
                } else if (!this$name.equals(other$name)) {
                    return false;
                }

                Object this$email = this.getEmail();
                Object other$email = other.getEmail();
                if (this$email == null) {
                    if (other$email != null) {
                        return false;
                    }
                } else if (!this$email.equals(other$email)) {
                    return false;
                }

                Object this$created = this.getCreated();
                Object other$created = other.getCreated();
                if (this$created == null) {
                    if (other$created != null) {
                        return false;
                    }
                } else if (!this$created.equals(other$created)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Customer;
    }

    public int hashCode() {
        int PRIME = true;
        int result = 1;
        Object $id = this.getId();
        int result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        Object $created = this.getCreated();
        result = result * 59 + ($created == null ? 43 : $created.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getName();
        return "Customer(name=" + var10000 + ", id=" + this.getId() + ", email=" + this.getEmail() + ", created=" + this.getCreated() + ")";
    }
}
```

Furthermore - these methods must be maintained as the Pojo evolves. If you add more fields, everything must be updated.

Use Lombok @Data annotation to make a very clean, elegant readable Pojo with high maintainability.

Hint: Lookup java doc on the @Data to see is usage


### Solution

```
@Data
public class Customer {
    private String name;
    private Long id;
    private String email;
    private Date created;
}
```