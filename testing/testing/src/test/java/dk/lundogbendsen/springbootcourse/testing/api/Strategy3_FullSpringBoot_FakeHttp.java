package dk.lundogbendsen.springbootcourse.testing.api;

import dk.lundogbendsen.springbootcourse.testing.model.Person;
import dk.lundogbendsen.springbootcourse.testing.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Kick off SpringBoot including AutoConfiguration including the WHOLE application an all of your Beans (@Component etc)
@SpringBootTest
// Set up a MockMvc client as a Spring Bean
@AutoConfigureMockMvc
public class Strategy3_FullSpringBoot_FakeHttp {

    @Autowired
    private MockMvc mvc;
    @Autowired
    ApplicationContext applicationContext;

    // Register a Mocked bean in the place of this type. This Mock will be Dependency Injected into the other Spring Beans
    @MockBean
    private PersonRepository personRepository;

    @BeforeEach
    public void init() {
        Person person = new Person();
        person.setName("Christian");
        person.setId(1L);
        given(personRepository.findById(1L)).willReturn(Optional.of(person));
    }

    @Test
    public void exampleTest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                get("/1").contentType(MediaType.APPLICATION_JSON);
        ResultActions perform = mvc.perform(requestBuilder);
        perform
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasKey("id")))
                .andExpect(jsonPath("$", hasKey("name")))
                .andExpect(jsonPath("$.name", is("Christian")));
    }

}
