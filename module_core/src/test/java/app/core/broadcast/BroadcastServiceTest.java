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

    @BeforeEach
    void setUp() {
        // Setup is handled by Mockito
    }

    @Test
    void testBroadcast_Success() {
        String message = "Test broadcast message";
        List<Long> chatIds = Arrays.asList(12345L, 67890L, 11111L);

        when(subscriberService.getActiveSubscribers()).thenReturn(chatIds);
        doNothing().when(sender).sendText(anyLong(), anyString());

        broadcastService.broadcast(message);

        verify(subscriberService, times(1)).getActiveSubscribers();
        verify(sender, times(3)).sendText(anyLong(), eq(message));
        verify(sender).sendText(12345L, message);
        verify(sender).sendText(67890L, message);
        verify(sender).sendText(11111L, message);
    }

    @Test
    void testBroadcast_EmptySubscribers() {
        String message = "Test broadcast message";
        List<Long> chatIds = List.of();

        when(subscriberService.getActiveSubscribers()).thenReturn(chatIds);

        broadcastService.broadcast(message);

        verify(subscriberService, times(1)).getActiveSubscribers();
        verify(sender, never()).sendText(anyLong(), anyString());
    }

    @Test
    void testBroadcast_ExceptionHandling() {
        String message = "Test broadcast message";
        List<Long> chatIds = Arrays.asList(12345L, 67890L);

        when(subscriberService.getActiveSubscribers()).thenReturn(chatIds);
        doThrow(new RuntimeException("Network error")).when(sender).sendText(eq(12345L), anyString());
        doNothing().when(sender).sendText(eq(67890L), anyString());

        // Should not throw exception, should handle gracefully
        broadcastService.broadcast(message);

        verify(subscriberService, times(1)).getActiveSubscribers();
        verify(sender, times(2)).sendText(anyLong(), eq(message));
    }

    @Test
    void testBroadcast_SingleSubscriber() {
        String message = "Test broadcast message";
        List<Long> chatIds = List.of(12345L);

        when(subscriberService.getActiveSubscribers()).thenReturn(chatIds);
        doNothing().when(sender).sendText(anyLong(), anyString());

        broadcastService.broadcast(message);

        verify(subscriberService, times(1)).getActiveSubscribers();
        verify(sender, times(1)).sendText(12345L, message);
    }
}

