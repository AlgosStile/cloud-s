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

@Epic("ServiceA Тестирование")
@Feature("Основные функции ServiceA")
@Story("Проверка базовых эндпоинтов сервиса")
public class ServiceAApiTest extends TestRunner {
    private static final String HELLO_ENDPOINT = TestConfig.getServiceAEndpoint();
    private static final String INVALID_ENDPOINT = "/serviceA/invalid";
    private static final String EXPECTED_CONTENT_TYPE = "text/plain;charset=UTF-8";
    private static final String EXPECTED_RESPONSE_TEXT = "Приветствую! Вы в приложении: App-1";

    @Test
    @DisplayName("Успешный ответ от /hello эндпоинта")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("Smoke")
    void shouldReturnValidGreeting_whenHelloEndpointCalled() {
        APIResponse response = LoadTestUtils.executeApiCall(HELLO_ENDPOINT);

        assertAll("Проверка ответа /hello",
                () -> ApiAssertions.assertStatusCode(response, 200),
                () -> ApiAssertions.assertResponseTextEquals(response, EXPECTED_RESPONSE_TEXT)
        );
    }

    @ParameterizedTest(name = "Проверка эндпоинта {0} → ожидаемый статус: {1}")
    @MethodSource("provideEndpointsAndStatuses")
    @DisplayName("Валидация доступности эндпоинтов")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Regression")
    void shouldReturnCorrectStatusWhenDifferentEndpointsCalled(String endpoint, int expectedStatus) {
        APIResponse response = LoadTestUtils.executeApiCall(endpoint);
        ApiAssertions.assertStatusCode(response, expectedStatus);
    }

    @Test
    @DisplayName("Проверка корректности заголовков ответа")
    @Severity(SeverityLevel.MINOR)
    @Tag("HeadersValidation")
    void shouldContainRequiredHeadersWhenRequestProcessed() {
        APIResponse response = LoadTestUtils.executeApiCall(HELLO_ENDPOINT);

        assertAll("Проверка заголовков ответа",
                () -> ApiAssertions.assertStatusCode(response, 200),
                () -> ApiAssertions.assertContentType(response, EXPECTED_CONTENT_TYPE),
                () -> ApiAssertions.assertHeaderExists(response, "date")
        );
    }

    //$ ===>>> region Вспомогательные методы
    private static Stream<Arguments> provideEndpointsAndStatuses() {
        return Stream.of(
                Arguments.of(HELLO_ENDPOINT, 200),
                Arguments.of(INVALID_ENDPOINT, 404)
        );
    }
}