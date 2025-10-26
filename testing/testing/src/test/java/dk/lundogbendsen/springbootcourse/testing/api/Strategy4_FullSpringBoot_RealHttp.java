package dk.lundogbendsen.springbootcourse.testing.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Strategy4_FullSpringBoot_RealHttp {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void exampleTest() {
        // This is a live request to the actual running springboot server. Data will be stored in a memory database (H2)
        ResponseEntity<Long> id = this.restTemplate.postForEntity("/", "Christian", Long.class);
        assertThat(id.getBody()).isEqualTo(1L);
    }
}
