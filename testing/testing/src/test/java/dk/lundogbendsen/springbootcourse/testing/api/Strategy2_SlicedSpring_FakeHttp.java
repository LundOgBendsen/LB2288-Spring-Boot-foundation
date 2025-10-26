package dk.lundogbendsen.springbootcourse.testing.api;

import dk.lundogbendsen.springbootcourse.testing.model.Person;
import dk.lundogbendsen.springbootcourse.testing.repository.PersonRepository;
import dk.lundogbendsen.springbootcourse.testing.service.MyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
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


// Sliced SpringContext
// Register the classes in the list as SpringBean
@WebMvcTest({PersonController.class}) // Tell slice to ONLY load the PersonController, and the other controllers.
// Filters are loaded (eg dk.lundogbendsen.springbootcourse.testing.api.LoggingFilter)
public class Strategy2_SlicedSpring_FakeHttp {

    @Autowired
    private MockMvc mvc;

    @Autowired
    ApplicationContext applicationContext;

    // Register a Mocked bean in the place of this type. This Mock will be Dependency Injected into the other Spring Beans
    @MockBean
    private PersonRepository personRepository;

    @SpyBean
    MyService myService;
    @BeforeEach
    public void init() {
        Person person = new Person();
        person.setName("Christian");
        person.setId(1L);
        given(personRepository.findById(1L)).willReturn(Optional.of(person));
    }

    @Test
    public void exampleTest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/1").contentType(MediaType.APPLICATION_JSON);
        ResultActions perform = mvc.perform(requestBuilder);
        perform
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasKey("id")))
                .andExpect(jsonPath("$", hasKey("name")))
                .andExpect(jsonPath("$.name", is("Christian")));
    }

}
