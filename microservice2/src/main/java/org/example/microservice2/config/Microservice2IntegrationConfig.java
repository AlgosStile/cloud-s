package org.example.microservice2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageChannel;

import java.io.File;

@Configuration
@EnableIntegration
public class Microservice2IntegrationConfig {

    @Bean
    public MessageChannel microservice2FileWriterChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "microservice2FileWriterChannel")
    public FileWritingMessageHandler fileWritingMessageHandler(
            @Value("${user.requests.filepath}") String directoryPath) {

        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        FileWritingMessageHandler handler = new FileWritingMessageHandler(directory);
        handler.setFileNameGenerator(message -> "requests.log");
        handler.setFileExistsMode(FileExistsMode.APPEND);
        handler.setAutoCreateDirectory(true);
        handler.setExpectReply(false);
        return handler;
    }
}

