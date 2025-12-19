package app.web.texts;

import app.module.node.texts.BotText;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TextSyncServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TextSyncService textSyncService;

    private String updateUrl = "http://localhost:8081/internal/update-module";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(textSyncService, "updateUrl", updateUrl);
        ReflectionTestUtils.setField(textSyncService, "rest", restTemplate);
    }

    @Test
    void testSendToBot_Success() {
        BotText botText = new BotText();
        botText.setId("TEST_TEXT");
        botText.setValue("Test value");

        when(restTemplate.postForObject(anyString(), any(), eq(String.class))).thenReturn("ok");

        textSyncService.sendToBot(botText);

        verify(restTemplate, times(1)).postForObject(
            eq(updateUrl),
            eq(botText),
            eq(String.class)
        );
    }

    @Test
    void testSendToBot_ExceptionHandling() {
        BotText botText = new BotText();
        botText.setId("TEST_TEXT");
        botText.setValue("Test value");

        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
            .thenThrow(new RuntimeException("Connection error"));

        // Should not throw exception, should handle gracefully
        try {
            textSyncService.sendToBot(botText);
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }

        verify(restTemplate, times(1)).postForObject(
            eq(updateUrl),
            eq(botText),
            eq(String.class)
        );
    }

    @Test
    void testSendToBot_WithDifferentText() {
        BotText botText1 = new BotText();
        botText1.setId("TEXT_1");
        botText1.setValue("Value 1");

        BotText botText2 = new BotText();
        botText2.setId("TEXT_2");
        botText2.setValue("Value 2");

        when(restTemplate.postForObject(anyString(), any(), eq(String.class))).thenReturn("ok");

        textSyncService.sendToBot(botText1);
        textSyncService.sendToBot(botText2);

        verify(restTemplate, times(2)).postForObject(anyString(), any(), eq(String.class));
        verify(restTemplate).postForObject(eq(updateUrl), eq(botText1), eq(String.class));
        verify(restTemplate).postForObject(eq(updateUrl), eq(botText2), eq(String.class));
    }
}

