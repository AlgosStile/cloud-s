package integration;

import com.microsoft.playwright.APIResponse;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tests.TestRunner;
import utils.ApiClient;
import utils.LoadTestUtils;
import utils.TestConfig;
import utils.assertions.ApiAssertions;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;


@Epic("Интеграционное тестирование Gateway")
@Feature("Маршрутизация запросов")
@Story("Проверка корректной работы Gateway")
public class GatewayIntegrationTest extends TestRunner {
    private static final String INVALID_ENDPOINT = TestConfig.getGatewayUrl() + "/invalid-endpoint";
    private static final String SERVICE_A_HELLO = TestConfig.getServiceAEndpoint();
    private static final String SERVICE_B_HELLO = TestConfig.getServiceBEndpoint();

    @BeforeAll
    static void setupApiClient() {
        ApiClient.setup(TestConfig.getGatewayUrl());
        Allure.addAttachment("Gateway URL", "text/plain", TestConfig.getGatewayUrl());
    }

    @ParameterizedTest(name = "Маршрут {0} → ответ сервиса")
    @MethodSource("provideServiceEndpoints")
    @DisplayName("Проверка корректной маршрутизации запросов")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("Integration")
    void shouldRouteToCorrectServiceWhenValidEndpointProvided(String endpoint, String expectedResponse) {
        logger.debug("Testing endpoint: {} in {} environment",
                TestConfig.getServiceAEndpoint(), TestConfig.getEnvironment());
        APIResponse response = LoadTestUtils.executeApiCall(endpoint);

        assertAll("Проверка ответа для эндпоинта: " + endpoint,
                () -> ApiAssertions.assertStatusCode(response, 200),
                () -> ApiAssertions.assertResponseTextEquals(response, expectedResponse),
                () -> ApiAssertions.assertUrlContainsPath(response, endpoint)
        );
    }

    @Test
    @DisplayName("Проверка обработки несуществующего маршрута")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("ErrorHandling")
    void shouldReturn404WhenInvalidRouteRequested() {
        logger.debug("Testing endpoint: {}", TestConfig.getServiceAEndpoint());
        APIResponse response = LoadTestUtils.executeApiCall(INVALID_ENDPOINT);

        assertAll("Проверка ответа для несуществующего пути",
                () -> ApiAssertions.assertStatusCode(response, 404),
                () -> ApiAssertions.assertResponseContains(response, "Not Found")
        );
    }

    private static Stream<Arguments> provideServiceEndpoints() {
        return Stream.of(
                Arguments.of(SERVICE_A_HELLO, "Приветствую! Вы в приложении: App-1"),
                Arguments.of(SERVICE_B_HELLO, "Приветствую! Вы в приложении: App-2")
        );
    }
}
