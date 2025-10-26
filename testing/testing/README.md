# Testing

## Exercises

We will be using the URL Shortener service as our test-case

If you don't have your own working version of the URL Shortener, you can use the solution here:

**api/urlshortener**

### Exercise 1: Setup test suite
In a maven project, the test suite is located in a folder structure that matches the test-target under the `src/main/test` folder. 
This folder is created for you if you make the project using Spring Initializr. And it includes 1 class that is an empty test class.

- Ensure that the `src/main/test` exists (create it if it does not).
- Delete any existing test cases.
- Create a new class called UserServiceTests in the same package as the UserService (but under the test-folder structure).
- Add this import statement: `import static org.junit.jupiter.api.Assertions.*;`
- Alternatively you can open the UserService class, right click and select "Generate..." -> "Test". This opens a Test generation Wizard that create the Test class.

Creating the test-target:
- Make a class field `private UserService userService`.

You now have a test suite with a test-target, but without any tests.

The test-target is currently null. There are several ways of initializing it.

1. Assign it new UserService() directly in the declaration.
2. Use an initialiser method annotated with @BeforeEach and assign it a new UserService.
3. Use MockitoExtension (@InjectMocks).
4. Use SpringExtension (@Autowired).

In the first few test exercises, we'll just use the most simple approach and assign it directly in the declaration:
- Do that.

#### Solution
```java
package dk.lundogbendsen.urlshortener.service;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService = new UserService();

}
```

### Exercise 2: make a test for creating a User
There are at least two test cases we want to try when creating a User:
1. Creating a user with unique username.
2. Creating a user with an existing username.


Creating a unique user:
- create a method: `public void createUniqueUserTest()`.
- Annotate it with `@Test`.
- Let the UserService create a user with userName=user1 and password=password1.
- Use `assertEquals()` to test that the user returned from the call indeed have a userName of user1 and a password of password1.
- Run the test case: right click on the method and and choose Run...

#### Solution
```java
@Test
public void createUniqueUserTest() {
    User user = userService.create("user1", "password1");
    assertEquals("user1", user.getUsername());
    assertEquals("password1", user.getPassword());
}
```


### Exercise 3: make a test for creating a User that already exists
We continue with another testcase for an already existing user. We will do it by creating the same user twice. The second creation should fail with a UserExistsException.

- create a method: `public void createNonUniqueUserTest()`.
- Annotate it with `@Test`.
- Let the UserService create a user with userName=user1 and password=password1.
- Use `assertEquals()` to test that the user returned from the call indeed have a userName of user1 and a password of password1.
- Create another user with the same userName (user1).
- Surround the second call in a try/catch(UserExistsException) with an empty catch block.
- As the last statement in the try block user fail() to fail the test. You are not supposed to come to this place because an exception is thrown in the line before.
- Run the test case: right click on the method and and choose Run...


#### Solution
```java
@Test
public void createNonUniqueUserTest() {
    User user = userService.create("user1", "password1");
    try {
        user = userService.create("user1", "password1");
        fail("A User with the same userName as an existing should throw an exception");
    } catch (UserExistsException e) {
    }
}
```

### Exercise 4: Delete a user - first try
- Make a new test method that tests deletion of a user.
- The test should set up a new user, fetch it, delete it and fetch it again. Like below: 
 ```java
    @Test
    public void deleteUserTest() {
        userService.create("user1", "password1");
        assertNotNull(userService.getUser("user1"));
        userService.delete("user1");
        assertThrows(UserNotFoundException.class, () -> userService.getUser("user1"));
    }
```
- ? When you run the test, why does it fail with a NullPointerException?


### Exercise 5: Delete a user - With Mocks
Exercise 4 did not work because we were using a UserService that depends on the TokenService in order to clean up tokens by the user. The TokenService was not injected, and therefore null.

To fix this, we will use a mock for the TokenService.

- Annotate the whole class with `@ExtendWith(MockitoExtension.class)`.
- Create a mock of TokenService
- Change initialization of the UserService to use @InjectMocks.

#### Solution
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private TokenService tokenService;
    @InjectMocks private UserService userService;
 
    @Test
    public void deleteUserTest() {
        userService.create("user1", "password1");
        User user = userService.getUser("user1");
        assertNotNull(user);
        userService.delete("user1");
        User userAfterDelete = userService.getUser("user1");
        assertNull(userAfterDelete);
    }
}
```

### Exercise 6: Prepare UserService with a canned answer
In order to test double-creation of a user, we created the user twice. 
An alternative would be to setup the "store" of the UserService to provide a user with the same userName as we try to create. 
We can use the when-thenReturn construction to set that up.

- The "store" of userService is a `HashMap<String, User>`. Declare that as a mock.
- In the createNonUniqueUserTest() add a when-thenReturn construction that returns true if the hashmaps.containsKey() is called with the argument "user1".
- Remove the statement that creates the user the first time (the one above the try/catch).

#### Solution
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock TokenService tokenService;
    @Mock HashMap<String, User> users;
    @InjectMocks UserService userService;

    @Test
    public void createNonUniqueUserTest() {
        when(users.containsKey("user1")).thenReturn(true);
        try {
            User user = userService.create("user1", "password1");
            fail("A User with the same userName as an existing should throw an exception");
        } catch (UserExistsException e) {
        }
    }
}
```

### Exercise 7: Verify that the TokenService was called when deleting a User
The unit test for deleting a user focuses on the UserService. But we want to make sure that it actually places a call to the TokenService to clean up theusers tokens.

For this we will verify() that the TokenService was called. We will also change the mock on the HashMap from previous exercise to be a @Spy instead of a @Mock.
This is because we generally wants the real functionality of the store, and only in some cases override it like we did before.

- Change the @Mock to @Spy on the users HashMap
- Add a verify() on the tokenService that test it's method deleteTokens() was called 1 time.

#### Solution
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock TokenService tokenService;
    @Spy HashMap<String, User> users;
    @InjectMocks UserService userService;

    @Test
    public void deleteUserTest() {
        userService.create("user1", "password1");
        User user = userService.getUser("user1");
        assertNotNull(user);
        userService.delete("user1");
        User userAfterDelete = userService.getUser("user1");
        assertNull(userAfterDelete);

        verify(tokenService, times(1)).deleteTokens(any());
    }
}
```

### Exercise 8: Inspect how the tokenService is being called (Capture arguments)
It is sometimes useful to not only verify that a dependency was called, but also HOW it was called. We can use @Capture for this purpose.

- Add a `@Captor ArgumentCaptor<User> userArgumentCaptor` to the class
- In the deleteUserTest add a verifyer on the tokenService that captures the arguments to the deleteTokens().
- Add an assert that test if the argument to deleteTokens() was the same as the original user

#### Solution
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock TokenService tokenService;
    @Spy HashMap<String, User> users;
    @InjectMocks UserService userService;
    @Captor ArgumentCaptor<User> userArgumentCaptor;

    @Test
    public void deleteUserTest() {
        userService.create("user1", "password1");
        User user = userService.getUser("user1");
        assertNotNull(user);
        userService.delete("user1");
        User userAfterDelete = userService.getUser("user1");
        assertNull(userAfterDelete);

        verify(tokenService, times(1)).deleteTokens(any());
        verify(tokenService).deleteTokens(userArgumentCaptor.capture());
        assertEquals(user, userArgumentCaptor.getValue());

    }
}
```

## Test TokenService

### Exercise 5: Test TokenService - create token with the name 'token' (fails)
### Exercise 6: Test TokenService - create token that already exists (fails)
### Exercise 7: Test TokenService - create token without a targetUrl (fails)
### Exercise 8: Test TokenService - create token with an invalid targetUrl (fails)
### Exercise 9: Test TokenService - create token with a targetUrl containing localhost (fails)
### Exercise 10: Test TokenService - create token with a legal targetUrl (success)


## Testing the Web Layer of URL Shortener
When testing the web layer, you must choose a testing strategy:
1. (S1) No web server using MockMvc (Standalone).
2. (S2) Fake web server using MockMvc and WebMvcTest (sliced Spring context).
3. (S3) Fake web server using MockMvc and Springboot  (full or sliced Spring context plus auto-configuration).
4. (S4) Real web server and RestTemplate (full Spring context).

In the following we will see how to setup each strategy to test the exact same API:
- The TokenController.create() 
- The FollowController.follow()

You can use your own implementation of the URL Shortener, or you can base the tests on the project provided here:

**api/urlshortener**

Just be aware that the solutions to the exercises are also implemented there.


### Exercise 1: Use S1 to test TokenController.create()
In this exercise you'll test proper handling of creating a token that already exists. We expect to receive a http status code of 409 (CONFLICT).

**Setup**

A lot of setup is required for this test to work. Hopefully it will make sense, once you get through it.
- Make a test class for testing TokenController.
- It should use the Mockito Extension.
- It should have a field of type MockMvc.
- It should setup the MockMvc as Standalone in a @BeforeEach method.
- Because all operations on TokenService require Security, you must also setup the SecurityIntercepter along with the UserService because the SecurityIntercepter uses that. 
  You probably want to let the mocked UserService return a valid user when it is asked for (when-thenReturn)
- You also need to setup the ExceptionHandlerAdvice because the create() throws exceptions.
- It should mock the TokenService.
- It should create a TokenController and inject mocks to it.
- It should setup the ExceptionHandlerAdvicer controller advice.

**Test**
- Create a test method that tests you get a http status code 409 when you try to create a token, that already exists.

**Tip**:
You can create a json String like this:
```java
Map<String, String> token = new HashMap<>();
token.put("token", "abc");
token.put("targetUrl", "https://dr.dk");
token.put("protectToken", "protectAbc");
JSONObject json = new JSONObject(token);
String content = json.toString();
```

#### Solution
```java
@ExtendWith(MockitoExtension.class)
class TokenControllerTest_S1 {
  @Mock UserService userService;
  @Mock TokenService tokenService;
  @InjectMocks TokenController tokenController;
  @InjectMocks SecurityIntercepter securityIntercepter;
  @InjectMocks ExceptionHandlerAdvicer exceptionHandlerAdvicer;

  private MockMvc mvc;

  @BeforeEach
  public void setup() {
    // MockMvc standalone approach
    mvc = MockMvcBuilders
            .standaloneSetup(tokenController)
            .addInterceptors(securityIntercepter)
            .setControllerAdvice(exceptionHandlerAdvicer)
            .build();

    when(userService.getUser("user1")).thenReturn(User.builder().username("user1").password("password1").build());
  }

  @Test
  public void createNonUniqueToken() throws Exception {
    when(tokenService.create(anyString(), anyString(), anyString(), anyString())).thenThrow(TokenAlreadyExistsException.class);

    Map<String, String> token = new HashMap<>();
    token.put("token", "abc");
    token.put("targetUrl", "https://dr.dk");
    token.put("protectToken", "protectAbc");
    JSONObject json = new JSONObject(token);
    final String content = json.toString();
    mvc.perform(
            post("/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content)
                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user1:password1".getBytes(StandardCharsets.UTF_8))))
            .andExpect(status().is(HttpStatus.CONFLICT.value()))
    ;
  }
}
```

### Exercise 2: Use S1 to test FollowController.follow()
In the exercise you'll setup a standalone MockMvc focusing on FollowController. 
You must create a test that demonstrates that the controller sends a proper redirect when given a valid token.

**Setup**

- Make a test class for testing FollowController.
- Use the Mockito Extension.
- Have a field of type MockMvc.
- Setup the MockMvc as Standalone in a @BeforeEach method.
- Mock the TokenService.
- Setup when-thenReturn on the tokenService so it returns a valid targetUrl given a token and a protectToken that is null.
- It should create a FollowController and inject mocks to it.

**Test**

- Test that you get a status code of 301 (MOVED PERMANENTLY).
- Test that you get a location header with the targetUrl setup in the when-thenReturn.

#### Solution
```java
@ExtendWith(MockitoExtension.class)
class FollowControllerTest {
    @Mock
    TokenService tokenService;
    @InjectMocks
    FollowController followController;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        // MockMvc standalone approach
        mvc = MockMvcBuilders
                .standaloneSetup(followController)
                .build();
    }

    @Test
    public void followToken() throws Exception {
        when(tokenService.resolveToken("abc", null)).thenReturn("https://dr.dk");
        mvc.perform(get("/abc"))
                .andExpect(status().is(HttpStatus.MOVED_PERMANENTLY.value()))
                .andExpect(header().string("location", "https://dr.dk"))
        ;
    }
}
```


### Exercise 3: Use S2 to test TokenController.create()  
Use Strategy 2 (Sliced Spring Context) to test create existing token.

- Annotate your test class with `@WebMvcTest`
- Then you can `@Autowired MockMvc mvc;`
- Otherwise look very similar to S1.

#### Solution
See **api/urlshortener/src/test/java/dk/lundogbendsen/urlshortener/api/TokenControllerTest_S2.java**

### Exercise 4: Use S2 to test FollowController.follow()
Use Strategy 2 (Sliced Spring Context) to test follow token.

- Annotate your test class with `@WebMvcTest`
- Then you can `@Autowired MockMvc mvc;`
- Otherwise look very similar to S1.

#### Solution
See **api/urlshortener/src/test/java/dk/lundogbendsen/urlshortener/api/FollowControllerTest_S2.java**

### Exercise 5: Use S3 to test TokenController.create()
Use Strategy 3 (Springboot Spring Context) to test create existing token.

- Annotate your test class with `@SpringBootTest` and `@AutoConfigureMockMvc`.
- Then you can `@Autowired MockMvc mvc;`
- Otherwise look very similar to S1.

#### Solution
See **api/urlshortener/src/test/java/dk/lundogbendsen/urlshortener/api/TokenControllerTest_S3.java**

### Exercise 6: Use S3 to test FollowController.follow()  
Use Strategy 3 (Springboot Spring Context) to test follow token.

- Annotate your test class with `@SpringBootTest` and `@AutoConfigureMockMvc`.
- Then you can `@Autowired MockMvc mvc;`
- Otherwise look very similar to S1.

#### Solution
See **api/urlshortener/src/test/java/dk/lundogbendsen/urlshortener/api/FollowControllerTest_S3.java**

### Exercise 7: Use S4 to test TokenController.create()
Use Strategy 4 (SpringBoot Spring context, Real Webserver, RestTemplate as client) to test create existing token.

- Use `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)` to setup a web server for your test.
- Declare a field `@Autowired TestRestTemplate restTemplate;`.
- Create a HttpEntity<Map> with the token and a header with Basic Authentication. **Tip:** 
```java
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user1", "password1");
        HttpEntity<Map> requestEntity = new HttpEntity<>(token, headers);
```
- Use the restTemplate to post for entity.
- Use assertEquals() to test for http status code is 409 (CONFLICT)

#### Solution
See api/urlshortener/src/test/java/dk/lundogbendsen/urlshortener/api/TokenControllerTest_S4.java

### Exercise 8: Use S4 to test FollowController.follow()  
Use Strategy 4 (SpringBoot Spring context, Real Webserver, RestTemplate as client) to test follow token.

- Use `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)` to setup a web server for your test.
- Declare a field `@Autowired TestRestTemplate restTemplate;`.
- Test that you get the right status code.
- Test that you get the right location header.

#### Solution
See **api/urlshortener/src/test/java/dk/lundogbendsen/urlshortener/api/FollowControllerTest_S4.java**
