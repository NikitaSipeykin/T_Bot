package app.module.program;

import app.core.payment.PaidPaymentInfo;
import app.core.payment.PaymentQueryService;
import app.core.program.DailyUpdateResult;
import app.module.program.dao.DailyLimit;
import app.module.program.dao.ProgramBlocks;
import app.module.program.dao.ProgramProgress;
import app.module.program.repo.DailyLimitRepo;
import app.module.program.repo.ProgramBlocksRepo;
import app.module.program.repo.ProgramProgressRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgramProgressServiceImplTest {

    @Mock
    private ProgramProgressRepo progressRepo;

    @Mock
    private ProgramBlocksRepo blocksRepo;

    @Mock
    private DailyLimitRepo dailyLimitRepo;

    @Mock
    private PaymentQueryService paymentQueryService;

    @InjectMocks
    private ProgramProgressServiceImpl programProgressService;

    private ProgramProgress programProgress;
    private PaidPaymentInfo paidPaymentInfo;
    private ProgramBlocks programBlock;
    private DailyLimit dailyLimit;

    @BeforeEach
    void setUp() {
        programProgress = new ProgramProgress();
        programProgress.setChatId(12345L);
        programProgress.setPaymentId(1L);
        programProgress.setProgressLevel("PROGRAM_BEGIN");

        paidPaymentInfo = new PaidPaymentInfo(1L, 12345L, LocalDateTime.now().minusDays(5));

        // Create ProgramBlocks using reflection since it has no setters
        programBlock = new ProgramBlocks();
        ReflectionTestUtils.setField(programBlock, "id", 1L);
        ReflectionTestUtils.setField(programBlock, "name", "PROGRAM_BEGIN");
        ReflectionTestUtils.setField(programBlock, "nextBlock", "PROGRAM_BEGIN_QUESTIONS");
        ReflectionTestUtils.setField(programBlock, "buttonText", "Start");

        // Create DailyLimit using reflection since it has no setters
        dailyLimit = new DailyLimit();
        ReflectionTestUtils.setField(dailyLimit, "dayNumber", 1);
        ReflectionTestUtils.setField(dailyLimit, "limitBlock", "PROGRAM_BEGIN");
    }

    @Test
    void testCreateIfNotExists_NewProgress() {
        Long chatId = 12345L;
        Long paymentId = 1L;

        when(progressRepo.findById(chatId)).thenReturn(Optional.empty());
        when(progressRepo.save(any(ProgramProgress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        programProgressService.createIfNotExists(chatId, paymentId);

        verify(progressRepo, times(1)).findById(chatId);
        verify(progressRepo, times(1)).save(any(ProgramProgress.class));
    }

    @Test
    void testCreateIfNotExists_ExistingProgress() {
        Long chatId = 12345L;
        Long paymentId = 1L;

        when(progressRepo.findById(chatId)).thenReturn(Optional.of(programProgress));

        programProgressService.createIfNotExists(chatId, paymentId);

        verify(progressRepo, times(1)).findById(chatId);
        verify(progressRepo, never()).save(any());
    }

    @Test
    void testGetProgress() {
        Long chatId = 12345L;

        when(progressRepo.findById(chatId)).thenReturn(Optional.of(programProgress));
        when(paymentQueryService.getPaidPayment(1L)).thenReturn(Optional.of(paidPaymentInfo));

        ProgramProgressDto result = programProgressService.getProgress(chatId);

        assertNotNull(result);
        assertEquals(chatId, result.chatId());
        assertEquals(paidPaymentInfo.paidAt().toLocalDate(), result.paymentDate());
        assertEquals("PROGRAM_BEGIN", result.progressLevel());
    }

    @Test
    void testGetProgress_UserNotFound() {
        Long chatId = 12345L;

        when(progressRepo.findById(chatId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            programProgressService.getProgress(chatId);
        });
    }

    @Test
    void testIsUserInProgram() {
        Long chatId = 12345L;

        when(progressRepo.existsById(chatId)).thenReturn(true);

        boolean result = programProgressService.isUserInProgram(chatId);

        assertTrue(result);
        verify(progressRepo, times(1)).existsById(chatId);
    }

    @Test
    void testIsUserInProgram_NotInProgram() {
        Long chatId = 12345L;

        when(progressRepo.existsById(chatId)).thenReturn(false);

        boolean result = programProgressService.isUserInProgram(chatId);

        assertFalse(result);
    }

    @Test
    void testGetCurrentBlock() {
        Long chatId = 12345L;

        when(progressRepo.findById(chatId)).thenReturn(Optional.of(programProgress));

        String result = programProgressService.getCurrentBlock(chatId);

        assertEquals("PROGRAM_BEGIN", result);
    }

    @Test
    void testGetCurrentButton() {
        Long chatId = 12345L;

        when(progressRepo.findById(chatId)).thenReturn(Optional.of(programProgress));
        when(blocksRepo.findByName("PROGRAM_BEGIN")).thenReturn(Optional.of(programBlock));

        String result = programProgressService.getCurrentButton(chatId);

        assertEquals("Start", result);
    }

    @Test
    void testGetCurrentButton_BlockNotFound() {
        Long chatId = 12345L;

        when(progressRepo.findById(chatId)).thenReturn(Optional.of(programProgress));
        when(blocksRepo.findByName("PROGRAM_BEGIN")).thenReturn(Optional.empty());

        String result = programProgressService.getCurrentButton(chatId);

        assertEquals("", result);
    }

    @Test
    void testGetTodayLimit() {
        Long chatId = 12345L;
        LocalDate paymentDate = LocalDate.now().minusDays(1);

        programProgress.setPaymentId(1L);
        paidPaymentInfo = new PaidPaymentInfo(1L, chatId, paymentDate.atStartOfDay());

        when(progressRepo.findById(chatId)).thenReturn(Optional.of(programProgress));
        when(paymentQueryService.getPaidPayment(1L)).thenReturn(Optional.of(paidPaymentInfo));
        when(dailyLimitRepo.findByDayNumber(2)).thenReturn(dailyLimit);

        String result = programProgressService.getTodayLimit(chatId);

        assertNotNull(result);
        assertEquals("PROGRAM_BEGIN", result);
    }

    @Test
    void testCanUserAccessBlock_CanAccess() {
        Long chatId = 12345L;
        String blockName = "PROGRAM_BEGIN";
        LocalDate paymentDate = LocalDate.now().minusDays(1);

        programProgress.setPaymentId(1L);
        paidPaymentInfo = new PaidPaymentInfo(1L, chatId, paymentDate.atStartOfDay());

        when(progressRepo.findById(chatId)).thenReturn(Optional.of(programProgress));
        when(paymentQueryService.getPaidPayment(1L)).thenReturn(Optional.of(paidPaymentInfo));
        when(dailyLimitRepo.findByDayNumber(2)).thenReturn(dailyLimit);
        // When blockName equals the limit block, isBlockBeforeOrEqual returns true immediately
        // without calling findByName, so we don't need to mock it

        boolean result = programProgressService.canUserAccessBlock(chatId, blockName);

        assertTrue(result);
    }

    @Test
    void testMoveToNextBlock() {
        Long chatId = 12345L;
        ProgramBlocks nextBlock = new ProgramBlocks();
        ReflectionTestUtils.setField(nextBlock, "name", "PROGRAM_BEGIN_QUESTIONS");

        when(progressRepo.findById(chatId)).thenReturn(Optional.of(programProgress));
        when(blocksRepo.findByName("PROGRAM_BEGIN")).thenReturn(Optional.of(programBlock));
        when(progressRepo.save(any(ProgramProgress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        programProgressService.moveToNextBlock(chatId);

        verify(progressRepo, times(1)).save(any(ProgramProgress.class));
    }

    @Test
    void testSetProgress() {
        Long chatId = 12345L;
        String blockName = "PROGRAM_BEGIN_QUESTIONS";

        when(progressRepo.findById(chatId)).thenReturn(Optional.of(programProgress));
        when(progressRepo.save(any(ProgramProgress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        programProgressService.setProgress(chatId, blockName);

        verify(progressRepo, times(1)).save(any(ProgramProgress.class));
    }

    @Test
    void testGetAllProgresses() {
        List<ProgramProgress> progresses = List.of(programProgress);

        when(progressRepo.findAll()).thenReturn(progresses);
        when(paymentQueryService.getPaidPayment(1L)).thenReturn(Optional.of(paidPaymentInfo));

        List<ProgramProgressDto> result = programProgressService.getAllProgresses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(12345L, result.get(0).chatId());
    }

    @Test
    void testDailyUpdate() {
        LocalDate paymentDate = LocalDate.now().minusDays(1);
        paidPaymentInfo = new PaidPaymentInfo(1L, 12345L, paymentDate.atStartOfDay());

        ProgramBlocks currentBlock = new ProgramBlocks();
        ReflectionTestUtils.setField(currentBlock, "id", 1L);
        ReflectionTestUtils.setField(currentBlock, "name", "PROGRAM_BEGIN");

        ProgramBlocks limitBlock = new ProgramBlocks();
        ReflectionTestUtils.setField(limitBlock, "id", 2L);
        ReflectionTestUtils.setField(limitBlock, "name", "PROGRAM_BEGIN_QUESTIONS");

        DailyLimit updatedLimit = new DailyLimit();
        ReflectionTestUtils.setField(updatedLimit, "dayNumber", 2);
        ReflectionTestUtils.setField(updatedLimit, "limitBlock", "PROGRAM_BEGIN_QUESTIONS");

        when(progressRepo.findAll()).thenReturn(List.of(programProgress));
        when(paymentQueryService.getPaidPayment(1L)).thenReturn(Optional.of(paidPaymentInfo));
        when(dailyLimitRepo.findByDayNumber(2)).thenReturn(updatedLimit);
        when(blocksRepo.findByName("PROGRAM_BEGIN")).thenReturn(Optional.of(currentBlock));
        when(blocksRepo.findByName("PROGRAM_BEGIN_QUESTIONS")).thenReturn(Optional.of(limitBlock));

        List<DailyUpdateResult> result = programProgressService.dailyUpdate();

        assertNotNull(result);
    }
}

