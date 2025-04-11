package org.example.microservice2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер, отвечающий за обработку запросов к микросервису 2.
 */
@RestController
@RequestMapping("/serviceB")
public class ApiController {

    @Autowired
    @Qualifier("microservice2FileWriterChannel")
    private MessageChannel fileWriterChannel;

    /**
     * Обрабатывает GET-запросы на /hello и записывает информацию о запросе в файл.
     *
     * @return Приветственное сообщение от микросервиса 2.
     */
    @GetMapping("/hello")
    public String hello() {
        Message<String> message = MessageBuilder
                .withPayload("MS2: User request: /hello")
                .build();
        fileWriterChannel.send(message);
        return "Приветствую! Вы в приложении: App-2";
    }
}


