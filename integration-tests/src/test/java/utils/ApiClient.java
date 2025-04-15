package utils;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import io.qameta.allure.Epic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Epic("API Client for Automated Testing")
public class ApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);
    private static APIRequestContext request;
    private static Playwright playwright;
    private static boolean initialized = false;

    public static void setup(String baseUrl) {
        if (initialized) {
            logger.warn("ApiClient уже инициализирован. Пропускаем повторную инициализацию.");
            return;
        }
        logger.info("Инициализация API клиента для {}", baseUrl);
        logger.debug("Таймаут соединения: {} мс", TestConfig.getRequestTimeout());
        playwright = Playwright.create();
        request = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(baseUrl)
                        .setTimeout(TestConfig.getRequestTimeout())
        );
        initialized = true;
    }

    public static APIResponse get(String endpoint) {
        checkInitialization();
        if (TestConfig.isDebug()) {
            logger.debug("GET {}", endpoint);
        }
        return request.get(endpoint);
    }

    public static APIResponse post(String endpoint) {
        checkInitialization();
        if (TestConfig.isDebug()) {
            logger.debug("POST {}", endpoint);
        }
        return request.post(endpoint);
    }

    public static APIResponse put(String endpoint) {
        checkInitialization();
        if (TestConfig.isDebug()) {
            logger.debug("PUT {}", endpoint);
        }
        return request.put(endpoint);
    }

    public static APIResponse patch(String endpoint) {
        checkInitialization();
        if (TestConfig.isDebug()) {
            logger.debug("PATCH {}", endpoint);
        }
        return request.patch(endpoint);
    }

    public static void teardown() {
        if (request != null) request.dispose();
        if (playwright != null) playwright.close();
        initialized = false;
        logger.info("API клиент остановлен");
    }

    private static void checkInitialization() {
        if (!initialized) {
            throw new IllegalStateException("ApiClient не инициализирован! Вызовите setup() перед использованием.");
        }
    }
}