package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CloudApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private RouteLocator routeLocator;

	@Test
	@DisplayName("integration test for interaction with app 1")
	public void gatewayRoutesToMicroservice1() {
		ResponseEntity<String> response = restTemplate.getForEntity("/serviceA/hello", String.class);
		assertEquals("Приветствую! Вы в приложении: App-1", response.getBody());
	}

	@Test
	@DisplayName("integration test for interaction with app 2")
	public void gatewayRoutesToMicroservice2() {
		ResponseEntity<String> response = restTemplate.getForEntity("/serviceB/hello", String.class);
		assertEquals("Приветствую! Вы в приложении: App-2", response.getBody());
	}
}




