package app.bot;

import app.bot.mane.BaseTelegramBot;
import app.module.node.texts.BotText;
import app.module.node.texts.BotTextRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternalControllerTest {

    @Mock
    private BotTextRepository repository;

    @Mock
    private BaseTelegramBot bot;

    @InjectMocks
    private InternalController internalController;

    private InternalSendRequest sendRequest;
    private BotText botText;

    @BeforeEach
    void setUp() {
        sendRequest = new InternalSendRequest();
        sendRequest.setChatId(12345L);
        sendRequest.setText("Test message");

        botText = new BotText();
        botText.setId("TEST_TEXT");
        botText.setValue("Test text value");
    }

    @Test
    void testInternalSend() {
        doNothing().when(bot).sendMessage(anyLong(), anyString(), isNull());

        String result = internalController.internalSend(sendRequest);

        assertEquals("ok", result);
        verify(bot, times(1)).sendMessage(sendRequest.getChatId(), sendRequest.getText(), null);
    }

    @Test
    void testInternalSend_WithNullButtons() {
        doNothing().when(bot).sendMessage(anyLong(), anyString(), isNull());

        String result = internalController.internalSend(sendRequest);

        assertEquals("ok", result);
        verify(bot).sendMessage(eq(sendRequest.getChatId()), eq(sendRequest.getText()), isNull());
    }

    @Test
    void testUpdateModule() {
        when(repository.save(any(BotText.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = internalController.update(botText);

        assertEquals("ok", result);
        verify(repository, times(1)).save(botText);
    }

    @Test
    void testUpdateModule_WithDifferentText() {
        BotText newText = new BotText();
        newText.setId("NEW_TEXT");
        newText.setValue("New value");

        when(repository.save(any(BotText.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = internalController.update(newText);

        assertEquals("ok", result);
        verify(repository, times(1)).save(newText);
    }
}

