package e2e;

import com.microsoft.playwright.APIResponse;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tests.TestRunner;
import utils.ApiClient;
import utils.LoadTestUtils;
import utils.TestConfig;
import utils.assertions.ApiAssertions;


@Epic("Обработка ошибок")
@Feature("Валидация ошибочных сценариев")
@Story("Проверка корректной обработки некорректных запросов")
public class ErrorHandlingTest extends TestRunner {
    private static final String MALFORMED_ENDPOINT = "/serviceA/hello?invalid=param%00";

    @ParameterizedTest(name = "Метод {0} для {1} → 405")
    @CsvSource({
            "POST, /serviceA/hello",
            "PUT, /serviceA/hello",
            "PATCH, /serviceA/hello"
    })
    @DisplayName("Проверка обработки недопустимых HTTP-методов")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Security")
    void testInvalidHttpMethods(String method, String endpoint) {
        APIResponse response = executeInvalidMethodCall(method, endpoint);
        ApiAssertions.assertStatusCode(response, 405);
    }

    @Test
    @DisplayName("Обработка некорректно сформированных запросов")
    @Severity(SeverityLevel.NORMAL)
    @Tag("Validation")
    void testMalformedRequests() {
        String malformedEndpoint = "/serviceA/hello?invalid=param%00";
        APIResponse response = LoadTestUtils.executeApiCall(MALFORMED_ENDPOINT);

        LoadTestUtils.logResponseDetails(response);
        ApiAssertions.assertStatusCode(response, 400);
    }

    private APIResponse executeInvalidMethodCall(String method, String endpoint) {
        return Allure.step("Выполнение " + method + " запроса", () -> {
            APIResponse response = switch (method) {
                case "POST" -> ApiClient.post(endpoint);
                case "PUT" -> ApiClient.put(endpoint);
                case "PATCH" -> ApiClient.patch(endpoint);
                default -> throw new IllegalArgumentException("Unsupported method: " + method);
            };
            LoadTestUtils.logResponseDetails(response);
            return response;
        });
    }
}
