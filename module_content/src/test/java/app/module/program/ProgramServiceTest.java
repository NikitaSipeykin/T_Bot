package app.module.program;

import app.core.payment.PaidPaymentInfo;
import app.core.program.DailyUpdateResult;
import app.core.program.ProgramMessage;
import app.module.node.texts.TextMarker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgramServiceTest {

    @Mock
    private ProgramProgressService progressService;

    @InjectMocks
    private ProgramService programService;

    @BeforeEach
    void setUp() {
        // Setup is handled by Mockito
    }

    @Test
    void testStartProgram_UserNotInProgram() {
        Long chatId = 12345L;
        String blockName = "PROGRAM_BEGIN";
        String buttonText = "Start";

        when(progressService.isUserInProgram(chatId)).thenReturn(false);
        when(progressService.getCurrentBlock(chatId)).thenReturn(blockName);
        when(progressService.canUserAccessBlock(chatId, blockName)).thenReturn(true);
        when(progressService.getCurrentButton(chatId)).thenReturn(buttonText);

        ProgramMessage result = programService.startProgram(chatId);

        assertNotNull(result);
        verify(progressService, times(1)).isUserInProgram(chatId);
    }

    @Test
    void testStartProgram_UserAlreadyInProgram() {
        Long chatId = 12345L;

        when(progressService.isUserInProgram(chatId)).thenReturn(true);

        ProgramMessage result = programService.startProgram(chatId);

        assertNull(result);
        verify(progressService, times(1)).isUserInProgram(chatId);
        verify(progressService, never()).getCurrentBlock(any());
    }

    @Test
    void testNextMessage_UserInProgram() {
        Long chatId = 12345L;
        // Use a block that ends with _QUESTIONS to test the auto-continue path
        String blockName = "PROGRAM_BEGIN_QUESTIONS";

        when(progressService.isUserInProgram(chatId)).thenReturn(true);
        // getCurrentBlock is called twice - once to check access, once to get the actual block
        when(progressService.getCurrentBlock(chatId))
            .thenReturn(blockName)  // First call
            .thenReturn(blockName); // Second call if canAccess is true
        when(progressService.canUserAccessBlock(chatId, blockName)).thenReturn(true);
        doNothing().when(progressService).moveToNextBlock(chatId);

        ProgramMessage result = programService.nextMessage(chatId);

        assertNotNull(result);
        // Blocks ending with _QUESTIONS should auto-continue
        assertTrue(result.shouldBeNext());
        verify(progressService, times(1)).isUserInProgram(chatId);
        verify(progressService, times(1)).moveToNextBlock(chatId);
    }

    @Test
    void testNextMessage_UserNotInProgram() {
        Long chatId = 12345L;

        when(progressService.isUserInProgram(chatId)).thenReturn(false);

        ProgramMessage result = programService.nextMessage(chatId);

        assertNull(result);
        verify(progressService, times(1)).isUserInProgram(chatId);
        verify(progressService, never()).getCurrentBlock(any());
    }

    @Test
    void testGetMessage_WithBeginMarker() {
        Long chatId = 12345L;
        String blockName = "PROGRAM_BEGIN" + TextMarker.BEGIN_MARKER;
        String buttonText = "Start";

        when(progressService.getCurrentBlock(chatId)).thenReturn(blockName);
        when(progressService.canUserAccessBlock(chatId, blockName)).thenReturn(true);
        when(progressService.getCurrentButton(chatId)).thenReturn(buttonText);

        // Access private method indirectly through startProgram
        when(progressService.isUserInProgram(chatId)).thenReturn(false);

        ProgramMessage result = programService.startProgram(chatId);

        assertNotNull(result);
        verify(progressService, times(1)).moveToNextBlock(chatId);
    }

    @Test
    void testGetMessage_WithIntroMarker() {
        Long chatId = 12345L;
        String blockName = "PROGRAM_INTRO" + TextMarker.INTRO_MARKER;
        String buttonText = "Continue";

        when(progressService.isUserInProgram(chatId)).thenReturn(true);
        when(progressService.getCurrentBlock(chatId)).thenReturn(blockName);
        when(progressService.canUserAccessBlock(chatId, blockName)).thenReturn(true);
        when(progressService.getCurrentButton(chatId)).thenReturn(buttonText);

        ProgramMessage result = programService.nextMessage(chatId);

        assertNotNull(result);
        assertFalse(result.shouldBeNext());
    }

    @Test
    void testGetMessage_WithQuestionsMarker() {
        Long chatId = 12345L;
        String blockName = "PROGRAM_QUESTIONS" + TextMarker.QUESTIONS_MARKER;

        when(progressService.isUserInProgram(chatId)).thenReturn(true);
        when(progressService.getCurrentBlock(chatId)).thenReturn(blockName);
        when(progressService.canUserAccessBlock(chatId, blockName)).thenReturn(true);

        ProgramMessage result = programService.nextMessage(chatId);

        assertNotNull(result);
        assertTrue(result.shouldBeNext());
        verify(progressService, times(1)).moveToNextBlock(chatId);
    }

    @Test
    void testGetMessage_TodayLimit() {
        Long chatId = 12345L;
        // Use a block name that doesn't end with _BEGIN to test the TODAY_LIMIT path
        // Blocks ending with _BEGIN return early before checking canAccess
        String blockName = "PROGRAM_INTRO";

        when(progressService.isUserInProgram(chatId)).thenReturn(true);
        // getCurrentBlock is called once at the start of getMessage
        when(progressService.getCurrentBlock(chatId)).thenReturn(blockName);
        // When canAccess is false, the code sets currentBlock to TODAY_LIMIT directly
        when(progressService.canUserAccessBlock(chatId, blockName)).thenReturn(false);

        ProgramMessage result = programService.nextMessage(chatId);

        assertNotNull(result);
        // When canAccess is false, the code should return TODAY_LIMIT
        assertEquals(TextMarker.TODAY_LIMIT, result.text());
        assertFalse(result.shouldBeNext());
    }

    @Test
    void testDailyUpdate() {
        List<DailyUpdateResult> expectedResults = List.of(
            new DailyUpdateResult(12345L, "PROGRAM_BEGIN_QUESTIONS")
        );

        when(progressService.dailyUpdate()).thenReturn(expectedResults);

        List<DailyUpdateResult> result = programService.dailyUpdate();

        assertNotNull(result);
        assertEquals(expectedResults, result);
        verify(progressService, times(1)).dailyUpdate();
    }

    @Test
    void testCheckUserAccessProgram() {
        Long chatId = 12345L;

        when(progressService.isUserInProgram(chatId)).thenReturn(true);

        boolean result = programService.checkUserAccessProgram(chatId);

        assertTrue(result);
        verify(progressService, times(1)).isUserInProgram(chatId);
    }

    @Test
    void testGrantAccess() {
        Long chatId = 12345L;
        Long paymentId = 1L;
        PaidPaymentInfo payment = new PaidPaymentInfo(paymentId, chatId, LocalDateTime.now());

        doNothing().when(progressService).createIfNotExists(chatId, paymentId);

        programService.grantAccess(payment);

        verify(progressService, times(1)).createIfNotExists(chatId, paymentId);
    }
}

