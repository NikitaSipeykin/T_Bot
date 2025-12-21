package app.core.broadcast;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriberServiceTest {

    @Mock
    private SubscriberRepository repo;

    @InjectMocks
    private SubscriberService subscriberService;

    @BeforeEach
    void setUp() {
        // Setup is handled by Mockito
    }

    @Test
    void testSubscribe() {
        Long chatId = 12345L;
        String username = "testuser";
        String firstName = "Test";

        subscriberService.subscribe(chatId, username, firstName);

        verify(repo, times(1)).saveOrActivate(chatId, username, firstName);
    }

    @Test
    void testUnsubscribe() {
        Long chatId = 12345L;

        subscriberService.unsubscribe(chatId);

        verify(repo, times(1)).deactivate(chatId);
    }

    @Test
    void testGetActiveSubscribers() {
        List<Long> expectedIds = Arrays.asList(12345L, 67890L);
        when(repo.findActiveChatIds()).thenReturn(expectedIds);

        List<Long> result = subscriberService.getActiveSubscribers();

        assertEquals(expectedIds, result);
        verify(repo, times(1)).findActiveChatIds();
    }

    @Test
    void testSetEmail() {
        Long chatId = 12345L;
        String email = "test@example.com";

        subscriberService.setEmail(chatId, email);

        verify(repo, times(1)).saveEmail(chatId, email);
    }

    @Test
    void testSetCode() {
        Long chatId = 12345L;
        String code = "123456";

        subscriberService.setCode(chatId, code);

        verify(repo, times(1)).setCode(chatId, code);
    }

    @Test
    void testGetCode() {
        Long chatId = 12345L;
        String expectedCode = "123456";
        when(repo.getVerificationCode(chatId)).thenReturn(expectedCode);

        String result = subscriberService.getCode(chatId);

        assertEquals(expectedCode, result);
        verify(repo, times(1)).getVerificationCode(chatId);
    }

    @Test
    void testSetVerified() {
        Long chatId = 12345L;

        subscriberService.setVerified(chatId);

        verify(repo, times(1)).verify(chatId);
    }

    @Test
    void testSetFinishedTest() {
        Long chatId = 12345L;

        subscriberService.setFinishedTest(chatId);

        verify(repo, times(1)).finishedTest(chatId);
    }

    @Test
    void testIsFinishedTesting() {
        Long chatId = 12345L;
        when(repo.isFinishedTest(chatId)).thenReturn(true);

        boolean result = subscriberService.isFinishedTesting(chatId);

        assertTrue(result);
        verify(repo, times(1)).isFinishedTest(chatId);
    }

    @Test
    void testIsFinishedTesting_ReturnsFalse() {
        Long chatId = 12345L;
        when(repo.isFinishedTest(chatId)).thenReturn(false);

        boolean result = subscriberService.isFinishedTesting(chatId);

        assertFalse(result);
        verify(repo, times(1)).isFinishedTest(chatId);
    }
}


