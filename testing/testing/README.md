# Testing

## Exercises

We will be using the url-shortner project as our test-case

### Exercise 1: Setup test suite

In a maven project, the test suite is located in a folder structure that matches the test-target under the
`src/test` folder.
This folder is created for you if you make the project using Spring Initializr. And it includes 1 class that is an empty
test class.

- Create a new class called UserServiceTests in the same package as the UserService (BUT under the test-folder
  structure).
- Add this import statement: `import static org.junit.jupiter.api.Assertions.*;`
- Alternatively you can open the UserService class, right click and select "Generate..." -> "Test". This opens a Test
  generation Wizard that create the Test class.

Creating the test-target:

- Make a class field `private UserService userService`.

You now have a test suite with a test-target, but without any tests.

The test-target is currently null. There are several ways of initializing it.

1. Assign it new UserService () directly in the declaration.
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
- Use `assertEquals()` to test that the user returned from the call indeed have a userName of user1 and a password of
  password1.
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

We continue with another testcase for an already existing user. We will do it by creating the same user twice. The
second creation should fail with a UserExistsException.

- create a method: `public void createNonUniqueUserTest()`.
- Annotate it with `@Test`.
- Let the UserService create a user with userName=user1 and password=password1.
- Create another user with the same userName (user1).
- Surround the second call in a try/catch (UserExistsException) with an empty catch block.
- As the last statement in the try block user fail () to fail the test. You are not supposed to come to this place
  because an exception is thrown in the line before.
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

- When you run the test, why does it fail with a NullPointerException?

### Exercise 5: Delete a user - With Mocks

Exercise 4 did not work because we were using a UserService that depends on the TokenService in order to clean up tokens
by the user. The TokenService was not injected, and therefore null.

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
An alternative would be to setup the "store" of the UserService to provide a user with the same userName as we try to
create.
We can use the when-thenReturn construction to set that up.

- The "store" of userService is a `HashMap<String, User>`. Declare that as a mock.
- In the createNonUniqueUserTest () add a when-thenReturn construction that returns true if the hashmaps.containsKey ()
  is called with the argument "user1".
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

The unit test for deleting a user focuses on the UserService. But we want to make sure that it actually places a call to
the TokenService to clean up theusers tokens.

For this we will verify () that the TokenService was called. We will also change the mock on the HashMap from previous
exercise to be a @Spy instead of a @Mock.
This is because we generally wants the real functionality of the store, and only in some cases override it like we did
before.

- Change the @Mock to @Spy on the users HashMap
- Add a verify () on the tokenService that test it's method deleteTokens () was called 1 time.

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

It is sometimes useful to not only verify that a dependency was called, but also HOW it was called. We can use @Capture
for this purpose.

- Add a `@Captor ArgumentCaptor<User> userArgumentCaptor` to the class
- In the deleteUserTest add a verifyer on the tokenService that captures the arguments to the deleteTokens ().
- Add an assert that test if the argument to deleteTokens () was the same as the original user

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

### Exercise 9: Standalone MockMvc Controller Testing with MockitoExtension

In this exercise, we test the web layer (`UrlShortnerController`) in complete isolation without bootstrapping a Spring ApplicationContext.
We use Mockito's `@ExtendWith(MockitoExtension.class)` alongside `@Mock` and `@InjectMocks`, and configure `MockMvc` using `MockMvcBuilders.standaloneSetup(...)`.

- Create a new test class `UrlShortnerControllerStandaloneTest` under `src/test/java/dk/lundogbendsen/urlshortner/controller/` (or in your test package).
- Annotate the test class with `@ExtendWith(MockitoExtension.class)`.
- Declare `@Mock private UserService userService;` and `@Mock private TokenService tokenService;`.
- Declare `@InjectMocks private UrlShortnerController urlShortnerController;`.
- Declare a `private MockMvc mockMvc;` field.
- In a `@BeforeEach` setup method, initialize `mockMvc` with `MockMvcBuilders.standaloneSetup(urlShortnerController).build()`.
- Add a test method `getUserTest()` that:
  - Mocks `userService.getUser("john")` to return a `User` (username="john", password="secret").
  - Performs a GET request to `/api/users/john` with `contentType(MediaType.APPLICATION_JSON)`.
  - Asserts that the HTTP status is `isOk()` (200), `jsonPath("$.username")` is `"john"`, and `jsonPath("$.password")` is `"secret"`.

#### Solution

```java
package dk.lundogbendsen.urlshortner.controller;

import dk.lundogbendsen.urlshortner.model.User;
import dk.lundogbendsen.urlshortner.service.TokenService;
import dk.lundogbendsen.urlshortner.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UrlShortnerControllerStandaloneTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UrlShortnerController urlShortnerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(urlShortnerController).build();
    }

    @Test
    void getUserTest() throws Exception {
        User user = User.builder().username("john").password("secret").build();
        when(userService.getUser("john")).thenReturn(user);

        mockMvc.perform(get("/api/users/john")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.password", is("secret")));
    }
}
```

### Exercise 10: Sliced Web MVC Testing with @WebMvcTest

Spring Boot provides the `@WebMvcTest` slice annotation to test Spring MVC controllers by configuring only web-related beans (controllers, Jackson mappers, filters) without loading the entire application context.
In this exercise, we will test `UrlShortnerController` using `@WebMvcTest` and mock its service dependencies with `@MockitoBean`.

- Create a new test class `UrlShortnerControllerWebMvcTest` in the `controller` package under `src/test/java`.
- Annotate the class with `@WebMvcTest(UrlShortnerController.class)`.
- Inject `MockMvc` using `@Autowired private MockMvc mockMvc;`.
- Declare mocked dependencies using `@MockitoBean private UserService userService;` and `@MockitoBean private TokenService tokenService;`.
- Add a test method `resolveTokenTest()` that:
  - Mocks `tokenService.resolveToken("my-token")` to return `"https://example.com"`.
  - Performs a GET request to `/api/tokens/my-token`.
  - Asserts that the HTTP status is `isOk()` (200) and the response body content is `"https://example.com"`.

#### Solution

```java
package dk.lundogbendsen.urlshortner.controller;

import dk.lundogbendsen.urlshortner.service.TokenService;
import dk.lundogbendsen.urlshortner.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlShortnerController.class)
class UrlShortnerControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TokenService tokenService;

    @Test
    void resolveTokenTest() throws Exception {
        when(tokenService.resolveToken("my-token")).thenReturn("https://example.com");

        mockMvc.perform(get("/api/tokens/my-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("https://example.com"));
    }
}
```

### Exercise 11: Testing JSON Request Body (POST endpoints) with @WebMvcTest

When building REST APIs, controllers frequently accept JSON payloads via `@RequestBody` and return newly created resources with HTTP 201 Created status.
In this exercise, you will test POST endpoints using `MockMvc` and verify JSON serialization/deserialization.

- In `UrlShortnerControllerWebMvcTest`, add a test method `createUserTest()`.
- Construct a JSON string representing a new user:
  ```json
  {
      "username": "alice",
      "password": "password123"
  }
  ```
- Mock `userService.create("alice", "password123")` to return a `User` instance with username `"alice"` and password `"password123"`.
- Use `mockMvc.perform(...)` with `post("/api/users")`, `.contentType(MediaType.APPLICATION_JSON)`, and `.content(requestJson)`.
- Assert that the response status is `isCreated()` (HTTP 201).
- Assert using `jsonPath` that `$.username` is `"alice"` and `$.password` is `"password123"`.
- (Optional) Add a test method `createTokenTest()` that posts a token JSON payload to `/api/tokens` and verifies the returned Token object with status `isCreated()`.

#### Solution

```java
@Test
void createUserTest() throws Exception {
    User createdUser = User.builder().username("alice").password("password123").build();
    when(userService.create("alice", "password123")).thenReturn(createdUser);

    String requestJson = """
            {
                "username": "alice",
                "password": "password123"
            }
            """;

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username", is("alice")))
            .andExpect(jsonPath("$.password", is("password123")));
}

@Test
void createTokenTest() throws Exception {
    User user = User.builder().username("alice").password("password123").build();
    Token createdToken = Token.builder()
            .token("spring")
            .targetUrl("https://spring.io")
            .protectToken("secret123")
            .user(user)
            .build();

    when(tokenService.create(eq("spring"), eq("https://spring.io"), eq("secret123"), any(User.class)))
            .thenReturn(createdToken);

    String requestJson = """
            {
                "token": "spring",
                "targetUrl": "https://spring.io",
                "protectToken": "secret123",
                "user": {
                    "username": "alice",
                    "password": "password123"
                }
            }
            """;

    mockMvc.perform(post("/api/tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token", is("spring")))
            .andExpect(jsonPath("$.targetUrl", is("https://spring.io")))
            .andExpect(jsonPath("$.user.username", is("alice")));
}
```
