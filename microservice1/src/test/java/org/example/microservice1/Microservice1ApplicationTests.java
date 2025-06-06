package org.example.microservice1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Microservice1ApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Uses TestRestTemplate to make a real HTTP")
    public void hello_ReturnsHelloMessage() {
        ResponseEntity<String> response = restTemplate.getForEntity("/serviceA/hello", String.class);
        assertThat(response.getBody()).isEqualTo("Приветствую! Вы в приложении: App-1");
    }


}
