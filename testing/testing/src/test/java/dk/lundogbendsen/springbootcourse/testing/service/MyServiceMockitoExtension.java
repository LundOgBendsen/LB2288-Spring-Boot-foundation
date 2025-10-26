package dk.lundogbendsen.springbootcourse.testing.service;

import dk.lundogbendsen.springbootcourse.testing.model.Person;
import dk.lundogbendsen.springbootcourse.testing.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MyServiceMockitoExtension {
//    @Rule public MockitoRule rule = MockitoJUnit.rule();

    @Mock private PersonRepository personRepository;

    @InjectMocks
    private MyService myService;

//    @BeforeEach
//    public void initMocks() {
//        MockitoAnnotations.initMocks(this);
//    }


    @BeforeEach
    public void init() {
        Person person = new Person();
        person.setName("Christian");
        person.setId(1L);
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
    }

    @Test
    public void testGetPerson() {
        Person person = myService.get(1L);
        assertEquals(person.getName(), "Christian");
    }

}
