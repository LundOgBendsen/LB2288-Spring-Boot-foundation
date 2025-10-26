package dk.lundogbendsen.springbootcourse.api.resttemplate.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/example")
public class ExampleController {

    @GetMapping
    public Object list() {
        return List.of(Map.of("name", "Christian", "age", 47));
    }

    @GetMapping("throws/{exceptionType}")
    public Object throwing(@PathVariable String exceptionType) throws Exception {
        if (exceptionType.equals("clientSideError")) {
            throw new UnsupportedMediaTypeStatusException("client side error");
        } else if (exceptionType.equals("internalServerError")) {
            throw new RuntimeException("internal server error");
        }
        return "Exception";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestParam String name, @RequestParam Long age) {

    }

    @PostMapping("pojo")
    @ResponseStatus(HttpStatus.CREATED)
    public Person createByPojo(@RequestBody Person person) {
        return person;
    }

    @GetMapping("/headers")
    public ResponseEntity list(@RequestHeader String apiToken) {
        if (apiToken == null || !apiToken.equals("123")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
