package utils;

import io.qameta.allure.Epic;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

@Epic("Test Configuration Management")
public class TestConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = TestConfig.class.getClassLoader()
                .getResourceAsStream("config/test.properties")) {
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить конфигурацию теста", e);
        }
        validateConfiguration();
    }

    private static void validateConfiguration() {
        List<String> requiredProps = List.of(
                "gateway.url",
                "servicea.endpoint",
                "serviceb.endpoint"
        );

        requiredProps.forEach(prop -> {
            if (props.getProperty(prop) == null && System.getenv(prop.toUpperCase()) == null) {
                throw new RuntimeException("Обязательный параметр не настроен: " + prop);
            }
        });

        if (getGatewayUrl().contains(" ")) {
            throw new IllegalArgumentException("Некорректный Gateway URL");
        }
    }

    public static String getGatewayDocsUrl() {
        return switch(getEnvironment()) {
            case "local" -> "http://localhost:8084/docs";
            case "docker" -> "http://gateway:8080/docs";
            case "staging" -> "https://staging-api/docs";
            default -> "https://prod-api/docs";
        };
    }

    public static String getGatewayUrl() {
        return System.getenv("GATEWAY_URL") != null
                ? System.getenv("GATEWAY_URL")
                : props.getProperty("gateway.url");
    }

    public static String getEnvironment() {
        return System.getenv("ENVIRONMENT") != null
                ? System.getenv("ENVIRONMENT")
                : props.getProperty("environment", "local");
    }

    public static String getServiceBEndpoint() {
        return System.getenv("SERVICEB_ENDPOINT") != null
                ? System.getenv("SERVICEB_ENDPOINT")
                : props.getProperty("serviceb.endpoint");
    }
    public static String getServiceAEndpoint() {
        return System.getenv("SERVICEA_ENDPOINT") != null
                ? System.getenv("SERVICEA_ENDPOINT")
                : props.getProperty("servicea.endpoint");
    }


    public static int getRequestTimeout() {
        String envTimeout = System.getenv("REQUEST_TIMEOUT");
        if (envTimeout != null) {
            return Integer.parseInt(envTimeout);
        }
        String propTimeout = props.getProperty("request.timeout");
        return propTimeout != null ? Integer.parseInt(propTimeout) : 7000;
    }

    public static String getBuildVersion() {
        return System.getenv("BUILD_VERSION") != null
                ? System.getenv("BUILD_VERSION")
                : props.getProperty("build.version");
    }

    public static boolean isDebug() {
        String envDebug = System.getenv("DEBUG");
        if (envDebug != null) {
            return Boolean.parseBoolean(envDebug);
        }
        String propDebug = props.getProperty("debug");
        return propDebug != null ? Boolean.parseBoolean(propDebug) : false;
    }
}