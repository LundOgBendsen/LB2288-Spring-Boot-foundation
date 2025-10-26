package dk.lundogbendsen.restassignmentapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/person")
public class PersonController {
//    @Autowired PersonService personService;
//
//    @GetMapping
//    public List<Person> list() {
//        return personService.list();
//    }
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public Person create(@RequestBody Person person) {
//        return personService.create(person);
//    }
//
//    @PutMapping
//    public Person update(@RequestBody Person person) {
//        return personService.update(person);
//    }
//
//    @GetMapping("/{id}")
//    public Person get(@PathVariable Long id) {
//        return personService.get(id);
//    }
//
//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void delete(@PathVariable Long id) {
//        personService.delete(id);
//    }
}
