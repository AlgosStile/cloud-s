package org.example.microservice2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

/**
 * Главный класс для запуска микросервиса 2.
 */
@SpringBootApplication
@EnableIntegration
public class Microservice2Application {
    public static void main(String[] args) {
        SpringApplication.run(Microservice2Application.class, args);
    }
}
