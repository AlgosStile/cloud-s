package api;

import com.microsoft.playwright.APIResponse;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.TestRunner;
import utils.LoadTestUtils;
import utils.TestConfig;
import utils.assertions.ApiAssertions;

import static org.junit.jupiter.api.Assertions.assertAll;

@Epic("ServiceB Тестирование")
@Feature("Основные функции ServiceB")
@Story("Проверка базовых эндпоинтов сервиса")
public class ServiceBApiTest extends TestRunner {
    private static final String HELLO_ENDPOINT = TestConfig.getServiceBEndpoint();
    private static final String EXPECTED_RESPONSE_TEXT = "Приветствую! Вы в приложении: App-2";

    @Test
    @DisplayName("Успешный ответ от /hello эндпоинта")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("Smoke")
    @Owner("API Team")
    void shouldReturnValidGreetingWhenHelloEndpointCalled() {
        final APIResponse response = LoadTestUtils.executeApiCall(HELLO_ENDPOINT);

        assertAll("Проверка ответа /hello",
                () -> ApiAssertions.assertStatusCode(response, 200),
                () -> ApiAssertions.assertResponseTextEquals(response, EXPECTED_RESPONSE_TEXT),
                () -> ApiAssertions.assertResponseTimeWithinLimit(response, TestConfig.getRequestTimeout())
        );
    }
}