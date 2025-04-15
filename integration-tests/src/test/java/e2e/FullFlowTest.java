package e2e;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.TestRunner;
import utils.ApiClient;
import utils.TestConfig;
import utils.assertions.ApiAssertions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;


@Epic("Интеграционное тестирование")
@Feature("Полный цикл работы системы")
@Story("Энд-ту-энд сценарии работы Gateway")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FullFlowTest extends TestRunner {
    private static final Logger logger = LoggerFactory.getLogger(FullFlowTest.class);
    private static final String SERVICE_A_HELLO = TestConfig.getServiceAEndpoint();
    private static final String INVALID_ENDPOINT = "/invalid-endpoint";
    private static final String SERVICE_A_INVALID = "/serviceA/invalid";
    private static final int PARALLEL_REQUESTS = 100;
    private static final int THREAD_POOL_SIZE = 20;
    private static final double MAX_FAILURE_RATE = 0.05;

    @Test
    @Order(1)
    @DisplayName("Проверка несуществующего эндпоинта")
    @Severity(SeverityLevel.BLOCKER)
    void shouldReturn404_whenInvalidEndpointRequested() {
        APIResponse response = ApiClient.get(INVALID_ENDPOINT);
        ApiAssertions.assertStatusCode(response, 404);
    }

    @Test
    @Order(2)
    @DisplayName("Проверка корректных заголовков ответа")
    @Severity(SeverityLevel.NORMAL)
    void shouldContainValidHeaders_whenValidRequestProcessed() {
        APIResponse response = ApiClient.get(SERVICE_A_HELLO);

        assertAll(
                () -> ApiAssertions.assertStatusCode(response, 200),
                () -> ApiAssertions.assertContentType(response, "text/plain;charset=UTF-8")
        );
    }

    @Test
    @Order(3)
    @DisplayName("Проверка несуществующего пути сервиса")
    @Severity(SeverityLevel.NORMAL)
    void shouldReturn404WithMessage_whenInvalidServicePathRequested() {
        APIResponse response = ApiClient.get(SERVICE_A_INVALID);

        assertAll(
                () -> ApiAssertions.assertStatusCode(response, 404),
                () -> ApiAssertions.assertResponseContains(response, "Not Found")
        );
    }

    @Test
    @Order(4)
    @DisplayName("Нагрузочное тестирование")
    @Severity(SeverityLevel.CRITICAL)
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    void shouldHandleLoad_whenMultipleRequestsSent() throws InterruptedException {
        logLoadTestParameters();

        ExecutorService executor = createThreadPool();
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        submitRequests(executor, successCount, failureCount);
        awaitTermination(executor);

        validateResults(successCount.get(), failureCount.get());
    }

    //$ ===>>> region Вспомогательные методы
    private ExecutorService createThreadPool() {
        return Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    private void submitRequests(ExecutorService executor, AtomicInteger success, AtomicInteger failure) {
        for (int i = 0; i < PARALLEL_REQUESTS; i++) {
            executor.submit(() -> processRequest(success, failure));
        }
    }

    private void processRequest(AtomicInteger success, AtomicInteger failure) {
        try (Playwright playwright = Playwright.create()) {
            APIRequestContext request = createRequestContext(playwright);
            APIResponse response = request.get(SERVICE_A_HELLO);

            if (response.status() == 200) {
                success.incrementAndGet();
            }
        } catch (Exception e) {
            failure.incrementAndGet();
            logger.error("Ошибка выполнения запроса: {}", e.getMessage());
        }
    }

    private APIRequestContext createRequestContext(Playwright playwright) {
        return playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(TestConfig.getGatewayUrl())
                        .setTimeout(TestConfig.getRequestTimeout())
        );
    }

    private void logLoadTestParameters() {
        Allure.addAttachment("Параметры теста", "text/plain",
                String.format(
                        "Количество запросов: %d%nТаймаут: %d мс%nПотоки: %d",
                        PARALLEL_REQUESTS,
                        TestConfig.getRequestTimeout(),
                        THREAD_POOL_SIZE
                )
        );
    }

    private void awaitTermination(ExecutorService executor) throws InterruptedException {
        boolean terminated = executor.awaitTermination(2, TimeUnit.MINUTES);
        if (!terminated) {
            logger.warn("Незавершенные задачи: {}", executor.shutdownNow().size());
        }
    }

    private void validateResults(int success, int failure) {
        int total = success + failure;
        double failureRate = (double) failure / PARALLEL_REQUESTS;

        assertAll(
                () -> assertEquals(PARALLEL_REQUESTS, total,
                        "Обработано запросов: %d из %d".formatted(total, PARALLEL_REQUESTS)),
                () -> assertTrue(failureRate <= MAX_FAILURE_RATE,
                        "Превышен допустимый уровень ошибок: %.1f%%".formatted(failureRate * 100))
        );
    }
    //$ ===>>>  endregion
}
