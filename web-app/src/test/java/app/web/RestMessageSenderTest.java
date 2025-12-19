package app.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestMessageSenderTest {

    private RestMessageSender restMessageSender;
    private String botInternalUrl = "http://localhost:8080/internal/send";

    @BeforeEach
    void setUp() {
        restMessageSender = new RestMessageSender(botInternalUrl);
    }

    @Test
    void testSendText() {
        Long chatId = 12345L;
        String text = "Test message";

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
            (mock, context) -> {
                when(mock.postForObject(anyString(), any(), eq(String.class))).thenReturn("ok");
            })) {

            RestMessageSender sender = new RestMessageSender(botInternalUrl);
            sender.sendText(chatId, text);

            RestTemplate restTemplate = mocked.constructed().get(0);
            verify(restTemplate, times(1)).postForObject(
                eq(botInternalUrl),
                any(),
                eq(String.class)
            );
        }
    }

    @Test
    void testSendText_PayloadContainsChatIdAndText() {
        Long chatId = 12345L;
        String text = "Test message";

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
            (mock, context) -> {
                when(mock.postForObject(anyString(), any(), eq(String.class))).thenReturn("ok");
            })) {

            RestMessageSender sender = new RestMessageSender(botInternalUrl);
            sender.sendText(chatId, text);

            RestTemplate restTemplate = mocked.constructed().get(0);
            verify(restTemplate).postForObject(
                eq(botInternalUrl),
                argThat(payload -> {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) payload;
                    return map.get("chatId").equals(chatId) && map.get("text").equals(text);
                }),
                eq(String.class)
            );
        }
    }

    @Test
    void testRestMessageSenderInitialization() {
        assertNotNull(restMessageSender);
    }
}

