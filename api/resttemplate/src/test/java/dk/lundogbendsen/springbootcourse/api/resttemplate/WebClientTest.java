package dk.lundogbendsen.springbootcourse.api.resttemplate;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

public class WebClientTest {
    String path = "/jokes/random";

    @Test
    public void testJokes() {
        WebClient webClient = WebClient.create("https://api.icndb.com");
        final String joke = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(path)
                        .queryParam("firstName", "Chuck")
                        .queryParam("lastName", "Norris")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jokeResponse -> jokeResponse.get("value").get("joke").asText())
                .block(Duration.ofSeconds(2));

        System.out.println("joke = " + joke);
    }
}
