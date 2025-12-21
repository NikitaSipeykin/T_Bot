package app.core.broadcast;

import app.core.MessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BroadcastServiceTest {

    @Mock
    private SubscriberService subscriberService;

    @Mock
    private MessageSender sender;

    @InjectMocks
    private BroadcastService broadcastService;

    @Test
    void testBroadcast_Success() {
        String message = "Test broadcast message";
        List<Long> chatIds = List.of(12345L, 67890L, 11111L);

        when(subscriberService.getActiveSubscribers()).thenReturn(chatIds);

        broadcastService.broadcast(message);

        verify(subscriberService).getActiveSubscribers();
        verify(sender).sendText(12345L, message);
        verify(sender).sendText(67890L, message);
        verify(sender).sendText(11111L, message);
        verifyNoMoreInteractions(sender);
    }

    @Test
    void testBroadcast_EmptySubscribers() {
        when(subscriberService.getActiveSubscribers()).thenReturn(List.of());

        broadcastService.broadcast("any");

        verify(subscriberService).getActiveSubscribers();
        verifyNoInteractions(sender);
    }

    @Test
    void testBroadcast_SingleSubscriber() {
        String message = "Test broadcast message";
        Long chatId = 12345L;

        when(subscriberService.getActiveSubscribers()).thenReturn(List.of(chatId));

        broadcastService.broadcast(message);

        verify(subscriberService).getActiveSubscribers();
        verify(sender).sendText(chatId, message);
        verifyNoMoreInteractions(sender);
    }

    @Test
    void testBroadcast_ExceptionIsPropagated() {
        String message = "Test broadcast message";
        List<Long> chatIds = List.of(12345L, 67890L);

        when(subscriberService.getActiveSubscribers()).thenReturn(chatIds);
        doThrow(new RuntimeException("Network error"))
            .when(sender).sendText(12345L, message);

        // ожидаем исключение — это текущий контракт сервиса
        RuntimeException ex = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> broadcastService.broadcast(message)
        );

        org.junit.jupiter.api.Assertions.assertEquals("Network error", ex.getMessage());

        verify(sender).sendText(12345L, message);
        verify(sender, never()).sendText(67890L, message);
    }
}


