package app.module.test;

import app.core.test.FinalMessage;
import app.core.test.OutgoingMessage;
import app.module.node.texts.BotText;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.test.dao.TestQuestion;
import app.module.test.dao.TestResult;
import app.module.test.dao.TestTopic;
import app.module.test.repo.TestQuestionRepository;
import app.module.test.repo.TestResultRepository;
import app.module.test.repo.TestTopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestServiceTest {

    @Mock
    private TestStateService stateService;

    @Mock
    private TestTopicRepository topicRepo;

    @Mock
    private TestQuestionRepository questionRepo;

    @Mock
    private TestResultRepository resultRepo;

    @Mock
    private BotTextService text;

    @InjectMocks
    private TestService testService;

    private TestTopic testTopic;
    private TestQuestion testQuestion;
    private BotText botText;
    private final Long chatId = 123456789L;

    @BeforeEach
    void setUp() {
        testTopic = new TestTopic();
        testTopic.setId(1L);
        testTopic.setName("Test Topic");
        testTopic.setOrderIndex(1);

        botText = new BotText();
        botText.setId("test_text");
        botText.setValue("Test question text?");

        testQuestion = new TestQuestion();
        testQuestion.setId(1L);
        testQuestion.setTopic(testTopic);
        testQuestion.setText(botText);
        testQuestion.setNum(1);
    }

    @Test
    void testStartTest_NewState() {
        TestState state = new TestState();
        state.setCurrentTopicIndex(1);
        state.setCurrentQuestionIndex(1);

        when(stateService.get(chatId)).thenReturn(null).thenReturn(state);
        when(topicRepo.findByOrderIndex(1)).thenReturn(Optional.of(testTopic));
        when(questionRepo.findByTopicIdAndNum(1L, 1)).thenReturn(Optional.of(testQuestion));

        OutgoingMessage result = testService.startTest(chatId);

        assertNotNull(result);
        verify(stateService, atLeastOnce()).put(eq(chatId), any(TestState.class));
        verify(topicRepo, times(1)).findByOrderIndex(1);
        verify(questionRepo, times(1)).findByTopicIdAndNum(1L, 1);
    }

    @Test
    void testStartTest_ExistingState() {
        TestState existingState = new TestState();
        existingState.setCurrentTopicIndex(1);
        existingState.setCurrentQuestionIndex(1);

        when(stateService.get(chatId)).thenReturn(existingState);
        when(topicRepo.findByOrderIndex(1)).thenReturn(Optional.of(testTopic));
        when(questionRepo.findByTopicIdAndNum(1L, 1)).thenReturn(Optional.of(testQuestion));

        OutgoingMessage result = testService.startTest(chatId);

        assertNotNull(result);
        verify(stateService, never()).put(any(), any());
    }

    @Test
    void testBuildNextQuestion() {
        TestState state = new TestState();
        state.setCurrentTopicIndex(1);
        state.setCurrentQuestionIndex(1);

        when(stateService.get(chatId)).thenReturn(state);
        when(topicRepo.findByOrderIndex(1)).thenReturn(Optional.of(testTopic));
        when(questionRepo.findByTopicIdAndNum(1L, 1)).thenReturn(Optional.of(testQuestion));

        OutgoingMessage result = testService.buildNextQuestion(chatId);

        assertNotNull(result);
        assertEquals("Test question text?", result.text());
        assertEquals(4, result.options().size());
        assertFalse(result.isNextTopic());
    }

    @Test
    void testBuildNextQuestion_NextTopic() {
        TestState state = new TestState();
        state.setCurrentTopicIndex(2);
        state.setCurrentQuestionIndex(1);

        when(stateService.get(chatId)).thenReturn(state);
        when(topicRepo.findByOrderIndex(2)).thenReturn(Optional.of(testTopic));
        when(questionRepo.findByTopicIdAndNum(1L, 1)).thenReturn(Optional.of(testQuestion));

        OutgoingMessage result = testService.buildNextQuestion(chatId);

        assertNotNull(result);
        assertTrue(result.isNextTopic());
    }

    @Test
    void testProcessAnswer() {
        String data = "TEST_Q_1_A_1";
        TestState state = new TestState();
        state.setCurrentTopicIndex(1);
        state.setCurrentQuestionIndex(1);

        when(stateService.get(chatId)).thenReturn(state);
        when(questionRepo.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(topicRepo.findByOrderIndex(1)).thenReturn(Optional.of(testTopic));
        when(questionRepo.findByTopicIdAndNum(1L, 2)).thenReturn(Optional.of(testQuestion));

        Object result = testService.processAnswer(chatId, data);

        assertNotNull(result);
        assertTrue(result instanceof OutgoingMessage);
        // processAnswer calls stateService.get() multiple times internally
        verify(stateService, atLeastOnce()).get(chatId);
    }

    @Test
    void testProcessAnswer_LastQuestionInTopic() {
        String data = "TEST_Q_1_A_1";
        TestState state = new TestState();
        state.setCurrentTopicIndex(1);
        state.setCurrentQuestionIndex(3);

        when(stateService.get(chatId)).thenReturn(state);
        when(questionRepo.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(topicRepo.findByOrderIndex(2)).thenReturn(Optional.of(testTopic));
        when(questionRepo.findByTopicIdAndNum(1L, 1)).thenReturn(Optional.of(testQuestion));

        Object result = testService.processAnswer(chatId, data);

        assertNotNull(result);
        // processAnswer calls stateService.get() multiple times internally
        verify(stateService, atLeastOnce()).get(chatId);
    }

    @Test
    void testProcessAnswer_LastTopic() {
        String data = "TEST_Q_1_A_1";
        TestState state = new TestState();
        state.setCurrentTopicIndex(7);
        state.setCurrentQuestionIndex(3);
        state.getTopicScores().put(1L, 5.0);

        TestTopic topic1 = new TestTopic();
        topic1.setId(1L);
        topic1.setName("Topic 1");

        when(stateService.get(chatId)).thenReturn(state);
        when(questionRepo.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(topicRepo.findById(1L)).thenReturn(Optional.of(topic1));

        Object result = testService.processAnswer(chatId, data);

        assertNotNull(result);
        assertTrue(result instanceof FinalMessage);
        verify(resultRepo, times(1)).save(any(TestResult.class));
        verify(stateService, times(1)).reset(chatId);
    }

    @Test
    void testGetScoreByAnswerId() {
        // Testing private method indirectly through processAnswer
        String data1 = "TEST_Q_1_A_1"; // Yes = 1.0
        String data2 = "TEST_Q_1_A_2"; // Sometimes = 0.5
        String data4 = "TEST_Q_1_A_4"; // No = 0.0

        TestState state = new TestState();
        state.setCurrentTopicIndex(1);
        state.setCurrentQuestionIndex(1);

        when(stateService.get(chatId)).thenReturn(state);
        when(questionRepo.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(topicRepo.findByOrderIndex(1)).thenReturn(Optional.of(testTopic));
        when(questionRepo.findByTopicIdAndNum(1L, 2)).thenReturn(Optional.of(testQuestion));

        // Test answer 1 (Yes)
        testService.processAnswer(chatId, data1);
        assertEquals(1.0, state.getTopicScores().get(1L));

        // Reset and test answer 2 (Sometimes)
        state.getTopicScores().clear();
        state.setCurrentQuestionIndex(1);
        testService.processAnswer(chatId, data2);
        assertEquals(0.5, state.getTopicScores().get(1L));

        // Reset and test answer 4 (No)
        state.getTopicScores().clear();
        state.setCurrentQuestionIndex(1);
        testService.processAnswer(chatId, data4);
        assertEquals(0.0, state.getTopicScores().get(1L));
    }

    @Test
    void testSaveResultTopics() {
        List<String> topics = List.of("Topic 1", "Topic 2");

        testService.saveResultTopics(chatId, topics);

        List<String> result = testService.getResultTopics(chatId);
        assertEquals(topics, result);
    }

    @Test
    void testGetResultTopics_NonExistent() {
        List<String> result = testService.getResultTopics(chatId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFinishTest_AllZero() {
        // Test that when all scores are zero, the test finishes with ALL_ZERO message
        String data = "TEST_Q_1_A_4"; // No = 0.0
        TestState state = new TestState();
        state.setCurrentTopicIndex(7);
        state.setCurrentQuestionIndex(3);
        state.getTopicScores().put(1L, 0.0);
        state.getTopicScores().put(2L, 0.0);

        TestTopic topic1 = new TestTopic();
        topic1.setId(1L);
        topic1.setName("Topic 1");
        TestTopic topic2 = new TestTopic();
        topic2.setId(2L);
        topic2.setName("Topic 2");

        when(stateService.get(chatId)).thenReturn(state);
        when(questionRepo.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(topicRepo.findById(1L)).thenReturn(Optional.of(topic1));
        when(topicRepo.findById(2L)).thenReturn(Optional.of(topic2));
        when(text.format(TextMarker.ALL_ZERO)).thenReturn("All zero message");

        Object result = testService.processAnswer(chatId, data);

        assertNotNull(result);
        assertTrue(result instanceof FinalMessage);
        FinalMessage finalMessage = (FinalMessage) result;
        assertEquals("All zero message", finalMessage.text());
        verify(stateService, times(1)).reset(chatId);
    }
}

