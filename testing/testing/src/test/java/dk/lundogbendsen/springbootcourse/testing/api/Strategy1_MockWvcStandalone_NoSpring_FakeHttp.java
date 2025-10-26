package dk.lundogbendsen.springbootcourse.testing.api;

import dk.lundogbendsen.springbootcourse.testing.model.Person;
import dk.lundogbendsen.springbootcourse.testing.service.MyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class Strategy1_MockWvcStandalone_NoSpring_FakeHttp {

    private MockMvc mvc;

    @Mock
    private MyService myService;

    @InjectMocks
    private PersonController personController;


    @BeforeEach
    public void setup() {
        // MockMvc standalone approach
        mvc = MockMvcBuilders.standaloneSetup(personController)
//                .setControllerAdvice(new controlleradvice...)
//                .addFilters(new filter...)
                .build();
    }

    @BeforeEach
    public void init() {
        Person person = new Person();
        person.setName("Christian");
        person.setId(1L);
        given(myService.get(1L)).willReturn(person);
    }

    @Test
    public void exampleTest() throws Exception {
        mvc.perform(get("/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasKey("id")))
                .andExpect(jsonPath("$", hasKey("name")))
                .andExpect(jsonPath("$.name", is("Christian")));
    }

}
