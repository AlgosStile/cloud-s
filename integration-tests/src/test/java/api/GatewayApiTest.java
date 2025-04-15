package api;

import com.microsoft.playwright.APIResponse;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tests.TestRunner;
import utils.LoadTestUtils;
import utils.TestConfig;
import utils.assertions.ApiAssertions;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;


@Epic("API Gateway Тестирование")
@Feature("Маршрутизация запросов")
@Story("Проверка корректной маршрутизации через Gateway")
public class GatewayApiTest extends TestRunner {
    private static final String INVALID_PATH = "/non-existing-service/hello";
    private static final String SERVICE_A_HELLO = TestConfig.getServiceAEndpoint();
    private static final String SERVICE_B_HELLO = TestConfig.getServiceBEndpoint();

    @ParameterizedTest(name = "Проверка маршрута {0} → ожидаемый ответ: {1}")
    @MethodSource("provideValidEndpoints")
    @DisplayName("Успешная маршрутизация запросов через Gateway")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("API Team")
    @Tag("Smoke")
    void shouldRouteRequestCorrectlyWhenValidEndpointProvided(String endpoint, String expectedResponse) {
        executeApiCallAndVerify(endpoint, expectedResponse);
    }

    @Test
    @DisplayName("Получение 404 при обращении к несуществующему пути")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Regression")
    void shouldReturn404WhenInvalidPathRequested() {
        final APIResponse response = LoadTestUtils.executeApiCall(INVALID_PATH);

        assertAll("Проверка ответа для несуществующего пути",
                () -> ApiAssertions.assertStatusCode(response, 404),
                () -> ApiAssertions.assertResponseContains(response, "Not Found")
        );
    }

    @Test
    @DisplayName("Проверка балансировки нагрузки между инстансами сервиса")
    @Severity(SeverityLevel.MINOR)
    @Tag("LoadTest")
    @Tag("Integration")
    void shouldBalanceLoadWhenMultipleRequestsSent() {
        Allure.step("Проверка распределения запросов", () -> {
            final APIResponse firstResponse = LoadTestUtils.executeApiCall(SERVICE_A_HELLO);
            final String serverHeader = firstResponse.headers().get("X-Server-ID");

            if (serverHeader != null) {
                LoadTestUtils.checkServerDistribution(SERVICE_A_HELLO);
            } else {
                LoadTestUtils.checkBasicAvailability(SERVICE_A_HELLO);
            }
        });
    }

    //$ ===>>> region Вспомогательные методы
    private static Stream<Arguments> provideValidEndpoints() {
        return Stream.of(
                Arguments.of(SERVICE_A_HELLO, "Приветствую! Вы в приложении: App-1"),
                Arguments.of(SERVICE_B_HELLO, "Приветствую! Вы в приложении: App-2")
        );
    }

    private void executeApiCallAndVerify(String endpoint, String expectedResponse) {
        Allure.step("Выполнение запроса к " + endpoint, () -> {
            final APIResponse response = LoadTestUtils.executeApiCall(endpoint);

            assertAll("Проверка корректности ответа для эндпоинта: " + endpoint,
                    () -> ApiAssertions.assertStatusCode(response, 200),
                    () -> ApiAssertions.assertResponseTextEquals(response, expectedResponse),
                    () -> ApiAssertions.assertUrlContainsPath(response, endpoint)
            );
        });
    }
}