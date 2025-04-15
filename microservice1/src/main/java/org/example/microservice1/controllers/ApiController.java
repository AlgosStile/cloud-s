package org.example.microservice1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * Контроллер, отвечающий за обработку запросов к микросервису 1.
 */
@RestController
@RequestMapping("/serviceA")
public class ApiController {

    @Autowired
    @Qualifier("microservice1FileWriterChannel")
    private MessageChannel fileWriterChannel;

    @GetMapping("/hello")
    public ResponseEntity<String> hello(@RequestParam Map<String, String> allParams) {
        if (containsInvalidParameters(allParams)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Недопустимые параметры запроса");
        }

        Message<String> message = MessageBuilder
                .withPayload("MS1: User request: /hello")
                .build();
        fileWriterChannel.send(message);

        return ResponseEntity.ok()
                .header(HttpHeaders.DATE, new Date().toString())
                .body("Приветствую! Вы в приложении: App-1");
    }

    private boolean containsInvalidParameters(Map<String, String> params) {
        return params.values().stream()
                .anyMatch(value -> value != null &&
                        (value.contains("\0") || value.matches(".*[\\\\<>\"'].*")));
    }
}