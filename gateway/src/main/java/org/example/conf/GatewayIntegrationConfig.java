package org.example.conf;

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

/**
 * Конфигурация Spring Integration для обработки сообщений.
 */
@Configuration
@EnableIntegration
public class GatewayIntegrationConfig {

    @Bean
    public MessageChannel gatewayFileWriterChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "gatewayFileWriterChannel")
    public FileWritingMessageHandler fileWritingMessageHandler(
            @Value("${gateway.requests.filepath}") String directoryPath) {

        File directory = new File(directoryPath);
        FileWritingMessageHandler handler = new FileWritingMessageHandler(directory);
        handler.setFileNameGenerator(message -> "gateway-requests.log");
        handler.setFileExistsMode(FileExistsMode.APPEND);
        handler.setAutoCreateDirectory(true);
        return handler;
    }
}
