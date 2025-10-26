package dk.lundogbendsen.springbootcourse.testing.api;

import dk.lundogbendsen.springbootcourse.testing.model.Person;
import dk.lundogbendsen.springbootcourse.testing.service.MyService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@RestController
public class PersonController {

    private MyService myService;

    public PersonController(MyService myService) {
        this.myService = myService;
    }

    @PostMapping("/")
    public Long createPerson(@RequestBody String name) {
        Person person = new Person();
        person.setName(name);

        Person createdPerson = myService.createPerson(person);
        return createdPerson.getId();
    }

    @GetMapping("/{id}")
    public Person get(WebRequest webRequest, @PathVariable Long id) {
        return myService.get(id);
    }
}
