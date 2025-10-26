package dk.lundogbendsen.api.controller;

import dk.lundogbendsen.api.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@Slf4j
public class MyRestController {
    @GetMapping
    public String helloWorld(@RequestParam String message) {
        return "Hello World: " + message;
    }

    @GetMapping("{id}")
    public Long pathMapping(@PathVariable Long id) {
        return id;
    }

    @PostMapping
    public void receivePerson(@RequestBody Person person) {
        System.out.println("person = " + person);
    }

    @PostMapping("/file")
    public String receiveFile(@RequestParam("file") MultipartFile file, @RequestHeader(name = "Content-Type") String contentType) throws IOException {
        final String content = new String(file.getBytes());
        log.debug("content = " + content);
        log.debug("contentType = " + contentType);
        return file.getOriginalFilename();
    }

    @GetMapping("throws")
    public String throwsException() {
        throw new RuntimeException("Hello");
    }

    @GetMapping("person")
    public Person getPerson() {
        Person p = new Person();
        p.setFirstName("Christian");
        p.setLastName("Jensen");
        return p;
    }

    @GetMapping("person-special")
    public ResponseEntity<Person> getPersonSpecial() {
        final Person person = new Person();
        person.setFirstName("Christian");
        person.setLastName("Jensen");
        HttpHeaders headers = new HttpHeaders();
        headers.add("TraceId", "abc123");
        final ResponseEntity<Person> ok = new ResponseEntity<>(person, headers, HttpStatus.OK);
        return ok;
    }


    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(String param) throws IOException {
        File file = new File("api/http-status-codes.png");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=http-status-codes.png")
                .body(resource);
    }

    @GetMapping("exception-strategy1")
    public void exceptionStrategy1() {
        log.debug("In Controller - I'll throw an exception");
        throw new ExceptionStrategy1Exception();
    }

    @GetMapping("exception-strategy2")
    public void exceptionStrategy2() {
        throw new ExceptionStrategy2Exception();
    }

    @GetMapping("exception-strategy3")
    public void exceptionStrategy3() {
        // This exception will be used for setting properties of the White-label error (status, message, trace)
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not Found", new RuntimeException("PersonNotFound"));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    public String exceptionHandlerStrategy1(ExceptionStrategy1Exception e) {
        log.debug("In ExceptionHandler - I'll take care of that exception");
        return "strategy1 from LOCAL HANDLER";
    }


}
