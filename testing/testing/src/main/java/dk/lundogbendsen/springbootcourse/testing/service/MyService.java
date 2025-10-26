package dk.lundogbendsen.springbootcourse.testing.service;

import dk.lundogbendsen.springbootcourse.testing.model.Person;
import dk.lundogbendsen.springbootcourse.testing.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyService {

    @Autowired
    private PersonRepository personRepository;

    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    public Person get(Long id) {
        return personRepository.findById(id).get();
    }
}
