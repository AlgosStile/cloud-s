package utils;

import com.microsoft.playwright.APIResponse;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.assertions.ApiAssertions;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Load Testing Utilities")
public class LoadTestUtils {
    private static final Logger logger = LoggerFactory.getLogger(LoadTestUtils.class);

    public static void checkServerDistribution(String endpoint) {
        List<String> servers = IntStream.range(0, 5)
                .mapToObj(i -> executeApiCall(endpoint))
                .map(response -> response.headers().get("X-Server-ID"))
                .filter(Objects::nonNull)
                .toList();

        long uniqueServers = servers.stream().distinct().count();

        assertAll(
                () -> assertFalse(servers.isEmpty(), "Не получены заголовки серверов"),
                () -> assertTrue(uniqueServers > 1,
                        "Запросы должны распределяться между разными серверами. Найдено: " + uniqueServers)
        );
    }

    public static void checkBasicAvailability(String endpoint) {
        IntStream.range(0, 5).forEach(i -> {
            APIResponse response = executeApiCall(endpoint);
            ApiAssertions.assertStatusCode(response, 200);
        });
        logger.warn("Проверка балансировки невозможна - отсутствует X-Server-ID");
    }

    public static APIResponse executeApiCall(String endpoint) {
        return Allure.step("Выполнение запроса к " + endpoint, () -> {
            Allure.addAttachment("Request", "text/plain", "GET " + endpoint);
            APIResponse response = ApiClient.get(endpoint);
            logResponseDetails(response);
            return response;
        });
    }

    public static void logResponseDetails(APIResponse response) {
        Allure.addAttachment("Response", "application/json", response.text());
        LoggerFactory.getLogger(LoadTestUtils.class)
                .debug("Response status: {}, body: {}", response.status(), response.text());
    }
}