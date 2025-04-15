package org.example;

import org.example.conf.SingletonBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.server.WebFilter;

/**
 * Главный класс для запуска API Gateway.
 */
@SpringBootApplication
public class CloudApplication {
    private final SingletonBean singletonBean;

    public CloudApplication() {
        this.singletonBean = SingletonBean.getInstance();
    }

    public static void main(String[] args) {
        SpringApplication.run(CloudApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("microservice1", r -> r.path("/serviceA/**")
                        .uri("http://localhost:8083"))
                .route("microservice2", r -> r.path("/serviceB/**")
                        .uri("http://localhost:8082"))
                .build();
    }
    @Bean
    public WebFilter corsFilter() {
        return new CorsWebFilter(source -> {
            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedOrigin("*");
            config.addAllowedMethod("*");
            config.addAllowedHeader("*");
            return config;
        });
    }
}

