# UrlShortener with persistence

## Exercises
We are added Persistence to the Url Shortener. 



### Exercise 1: Add required dependencies
In order to get JPA support, we must add the Springboot starter spring-boot-starter-data-jpa.
We must also add some sort of a database. We will use the embedded database H2.

- Add `spring-boot-starter-data-jpa` and `h2` as dependencies to the pom.xml.
- By default it is included the `h2` dependency is included with scope test, meaning that its classes are only available for tests. You must therefore change the scope to runtime.

#### Solution
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```


### Exercise 2: Turn Domain Model into Entity Model
We have a Domain model of two classes: User and Token. The Token references the User class, so this is a Many2One relation. Add the JPA markup on the domain model so it becomes an Entity model.

- Annotate both classes with @Entity
- With JPA you have a choice of generating the primary key yourself, or tell JPA to handle it for you. In this case we know that username and token MUST be unique, and therefore they are good candidates for a primary key.
  Add annotation `@Id` the fields username and token.
- If you are using Lomboks `@Builder`, you must also add the `@NoArgsConstructor` and `@AllArgsConstructor`.
- The relation to a User from the Token class must be a `@ManyToOne`.
- Add `spring.jpa.show-sql=true` to the application configuration in order to see the SQL that is generated.
- Run the application.
- ? Do you see the Hibernate output in the console? 

#### Solution
```java
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    String token;
    String protectToken;
    String targetUrl;
    @ManyToOne
    User user;
}
```

```java
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    String username;
    String password;
}
```

```properties
spring.jpa.show-sql=true
```

### Exercise 3: Define the Repositories
Now it is time to define the repositories for the entity model.

You must define 2 repositories because the entity model has 2 entities. 

- Create a Repository for Token.
- Create a Repository for User.

#### Solution

```java
public interface UserRepository extends JpaRepository<User, String> {
}
```

```java
public interface TokenRepository extends JpaRepository<Token, String> {
}
```


### Exercise 4: Update the UserService to use Repositories as store.
At this point everything is ready for storing Users and Tokens. In this exercise we will replace the HashMap storage in the Service Layer with real database persistence.

- Introduce a new autowired field for the UserRepository.
- Delete the HashMap that represents the users.
- Update the code to use the userRepository everywhere so the semantics remain the same.

**Tips:**

- findById() returns an Optional<User> instead of a User. You can test the Optional to see if a user was found or not, and then take appropriate actions.

#### Solution
```java
@Service
public class UserService {
    @Autowired
    private TokenService tokenService;
    @Autowired
    UserRepository userRepository;

    public User create(String userName, String password) {
        Optional<User> exitingUser = userRepository.findById(userName);
        if (exitingUser.isPresent()) {
            throw new UserExistsException();
        }
        User user = User.builder().username(userName).password(password).build();
        userRepository.save(user);
        return user;
    }

    public void delete(String userName) {
        Optional<User> user = userRepository.findById(userName);
        if (user.isPresent()) {
            tokenService.deleteTokens(user.get());
            userRepository.delete(user.get());
        }
    }

    public User getUser(String userName) {
        return userRepository.findById(userName).orElse(null);
    }
}
```

### Exercise 5: Update the TokenService to use Repositories as store.
In this exercise we will replace the HashMap storage in the Service Layer with real database persistence for the TokenService.

This exercise is a bit more tricky that for the UserService. You are going to find out that you are missing some functionality on the TokenRepository, which you must implement. By implementing this missing functionality, you are going to enjoy how much simpler and more readable the TokenService will be.

**Tips:**
- Identify what functionality you are missing simply by starting updating the TokenService and see where the holes are.
- The missing functionality can be implemented in a lot of different ways. In this exercise we will settle for the Property Expression approach, but you might as weel use @Query.
- The Repository framework is incredible flexible in understanding your intentions. You can choose to return a Token or an Optional<Token>.
- When you find yourself in need for a method on the tokenRepository, try to "think" like a repository - how should the method signature look? Return type, Naming, Parameters. They all influence the functionality.

**Tests:**
- Introduce a new autowired field for the TokenRepository.
- Delete the HashMap that represents the tokens.
- Update the code to use the tokenRepository everywhere so the semantics remain the same with one minor change. In the solution the semantics are not quite the same. The solution no longer throws AccessDeniedException. Instead it throws TokenNotFoundExceptions if the user does not own a token.


#### Solution
See **persistence/persistence-urlshortener/src/main/java/dk/lundogbendsen/urlshortener/service/TokenService.java**

See **persistence/persistence-urlshortener/src/main/java/dk/lundogbendsen/urlshortener/repository/TokenRepository.java**

### Exercise 6: Define a test for the data layer for TokenRepository
Since we are not just using a Repository out of the box, we want to test that our custom functionality works as expected. In this exercise we are using a Sliced Context given by the @DataJpaTest annotation.

To make it easier to test, here are some initializing data to include in a data.sql file, you might want to load. 
They will give you a bit of variance to play with:
```sql
insert into user values('user1', 'password1');
insert into user values('user2', 'password2');
insert into user values('user3', 'password3');

insert into token(token, protect_token, target_url, user_username) values('token1', null, 'https://dr.dk', 'user1');

insert into token(token, protect_token, target_url, user_username) values('token2', 'pt2', 'https://dr.dk', 'user2');
insert into token(token, protect_token, target_url, user_username) values('token3', 'pt2', 'https://dr.dk', 'user2');
insert into token(token, protect_token, target_url, user_username) values('token4', 'pt2', 'https://dr.dk', 'user2');

insert into token(token, protect_token, target_url, user_username) values('token5', 'pt3', 'https://dr.dk', 'user3');
insert into token(token, protect_token, target_url, user_username) values('token6', null, 'https://dr.dk', 'user3');
insert into token(token, protect_token, target_url, user_username) values('token7', null, 'https://dr.dk', 'user3');
```

- Make a test class for the TokenRepository.
- Annotate it with @DataJpaTest.
- Autowire tokenRepository and userRepository. The latter is required because Tokens need a User in the database.
- Before each test, save a User that can be used for saving and retrieving Tokens.
- Implement the following tests that targets each of the custom methods in the tokenRepository (use equivalent names in YOUR implementation):
```java
    @Test
    public void findAllByUserTest() {

    }
    @Test
    public void findByTokenAndUserTest() {

    }
    @Test
    public void findByTokenAndProtectTokenTest() {

    }
    @Test
    public void deleteAllByUserTest() {

    }
    @Test
    public void deleteByTokenAndUserTest() {

    }
```


#### Solution
See **persistence/persistence-urlshortener/src/test/java/dk/lundogbendsen/urlshortener/repository/TokenRepositoryTest.java**

### Exercise 6: Define a test for the UserService
We are now at a state where we think persistence is working. Let's make sure it works.  

In this exercise we will be testing the service layer with the new Repositories. We are going to make some integration tests.

For this we need a Spring Context. Since this is an integration test, the easiest thing is to use the @SpringBootTest annotation. This will load the whole Spring context including setting up H2 as embedded database.

- Create a test class for UserService.
- Annotate it with @SpringBootTest.
- Create a field an Autowired UserService.
- Create a test method that creates a User through the userService, and retrieve it again.
- Assert that the retrieved user contains the same properties as the saved one.

#### Solution
```java
@SpringBootTest
class UserServiceTest {
    @Autowired UserService userService;

    @Test
    public void createUserTest() {
        final User user = userService.create("user1", "password1");
        final User getUser = userService.getUser("user1");
        assertEquals("password1", getUser.getPassword());
    }
}
```


### Exercise 7: Define a test for the TokenService
We continue the testing with TokenService.

- Create a Test class for TokenService.
- Make the following test methods:
```java
    @Test
    @DisplayName("create token with the name 'token' (fails)")
    public void testCreateTokenWithTheNameToken() {

    }
    @Test
    @DisplayName("create token that already exists (fails)")
    public void testCreateTokenThatAlreadExists() {

    }
    @Test
    @DisplayName("create token without a targetUrl (fails)")
    public void testCreateTokenWithoutTargetUrl() {

    }
    @Test
    @DisplayName("create token with an invalid targetUrl (fails)")
    public void testCreateTokenWithInvalidTargetUrl() {

    }
    @Test
    @DisplayName("create token with a targetUrl containing localhost (fails)")
    public void testCreateTokenWithTargetUrlContainingLocalhost() {

    }

    @Test
    @DisplayName("create token with a legal targetUrl (success)")
    public void testCreateTokenWithLocalTargetUrl() {

    }
```

#### Solution
See **persistence/persistence-urlshortener/src/test/java/dk/lundogbendsen/urlshortener/service/TokenServiceTest.java**


