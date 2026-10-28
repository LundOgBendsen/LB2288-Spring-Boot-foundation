package dk.lundogbendsen.urlshortner.controller;

import dk.lundogbendsen.urlshortner.model.Token;
import dk.lundogbendsen.urlshortner.model.User;
import dk.lundogbendsen.urlshortner.service.TokenService;
import dk.lundogbendsen.urlshortner.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
}
