package utils.assertions;

import com.microsoft.playwright.APIResponse;
import io.qameta.allure.Epic;

import static org.junit.jupiter.api.Assertions.*;

@Epic("API Response Assertions")
public class ApiAssertions {

    public static void assertContentType(APIResponse response, String expectedType) {
        assertEquals(expectedType, response.headers().get("content-type"),
                "Content-Type должен быть " + expectedType);
    }

    public static void assertHeaderExists(APIResponse response, String header) {
        assertNotNull(response.headers().get(header),
                "Заголовок " + header + " должен присутствовать");
    }

    public static void assertStatusCode(APIResponse response, int expected) {
        assertEquals(expected, response.status(), "Неверный статус код");
    }

    public static void assertStatusCode(APIResponse response) {
        assertEquals(200, response.status(), "Неверный статус код");
    }

    public static void assertResponseTextEquals(APIResponse response, String expected) {
        assertEquals(expected, response.text(), "Текст ответа не соответствует ожидаемому");
    }

    public static void assertUrlContainsPath(APIResponse response, String path) {
        assertTrue(response.url().contains(path), "URL ответа содержит неверный путь");
    }

    public static void assertResponseContains(APIResponse response, String expectedText) {
        assertTrue(response.text().contains(expectedText), "Ответ должен содержать текст: " + expectedText);
    }

    public static void assertResponseTimeWithinLimit(APIResponse response, long limit) {
        long responseTime = response.headers().containsKey("x-response-time")
                ? Long.parseLong(response.headers().get("x-response-time"))
                : 0;

        assertTrue(responseTime <= limit,
                "Время ответа превышает лимит: " + responseTime + "ms");
    }
}