package app.bot;

import app.bot.mane.BaseTelegramBot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramMessageSenderTest {

    @Mock
    private BaseTelegramBot bot;

    @InjectMocks
    private TelegramMessageSender telegramMessageSender;

    @BeforeEach
    void setUp() {
        // Setup is handled by Mockito
    }

    @Test
    void testSendText() {
        Long chatId = 12345L;
        String text = "Test message";

        doNothing().when(bot).sendMessage(eq(chatId), eq(text), isNull());

        telegramMessageSender.sendText(chatId, text);

        verify(bot, times(1)).sendMessage(chatId, text, null);
    }

    @Test
    void testSendText_WithDifferentChatId() {
        Long chatId1 = 12345L;
        Long chatId2 = 67890L;
        String text = "Test message";

        doNothing().when(bot).sendMessage(anyLong(), anyString(), isNull());

        telegramMessageSender.sendText(chatId1, text);
        telegramMessageSender.sendText(chatId2, text);

        verify(bot, times(2)).sendMessage(anyLong(), eq(text), isNull());
        verify(bot).sendMessage(chatId1, text, null);
        verify(bot).sendMessage(chatId2, text, null);
    }
}

