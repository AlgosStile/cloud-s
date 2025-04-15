package integration;

import com.microsoft.playwright.APIResponse;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.TestRunner;
import utils.LoadTestUtils;
import utils.TestConfig;
import utils.assertions.ApiAssertions;


@Epic("ServiceA Тестирование")
@Feature("Интеграционные тесты")
@Story("Проверка базовой функциональности ServiceA")
public class ServiceAIntegrationTest extends TestRunner {
    private static final String SERVICE_A_HELLO_ENDPOINT = TestConfig.getServiceAEndpoint();

    @Test
    @DisplayName("Проверка доступности основного эндпоинта")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("Smoke")
    void shouldReturnSuccessStatusWhenValidEndpointCalled() {
        APIResponse response = LoadTestUtils.executeApiCall(SERVICE_A_HELLO_ENDPOINT);
        ApiAssertions.assertStatusCode(response);
    }
}
