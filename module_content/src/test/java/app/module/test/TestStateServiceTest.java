package app.module.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestStateServiceTest {

    private TestStateService testStateService;

    @BeforeEach
    void setUp() {
        testStateService = new TestStateService();
    }

    @Test
    void testGetOrCreate_NewState() {
        Long chatId = 12345L;

        TestState state1 = testStateService.getOrCreate(chatId);
        TestState state2 = testStateService.getOrCreate(chatId);

        assertNotNull(state1);
        assertNotNull(state2);
        assertSame(state1, state2); // Should return the same instance
    }

    @Test
    void testGetOrCreate_DifferentChatIds() {
        Long chatId1 = 12345L;
        Long chatId2 = 67890L;

        TestState state1 = testStateService.getOrCreate(chatId1);
        TestState state2 = testStateService.getOrCreate(chatId2);

        assertNotNull(state1);
        assertNotNull(state2);
        assertNotSame(state1, state2); // Should return different instances
    }

    @Test
    void testGet_ExistingState() {
        Long chatId = 12345L;
        TestState expectedState = testStateService.getOrCreate(chatId);

        TestState actualState = testStateService.get(chatId);

        assertNotNull(actualState);
        assertSame(expectedState, actualState);
    }

    @Test
    void testGet_NonExistentState() {
        Long chatId = 12345L;

        TestState state = testStateService.get(chatId);

        assertNull(state);
    }

    @Test
    void testReset() {
        Long chatId = 12345L;
        testStateService.getOrCreate(chatId);

        assertNotNull(testStateService.get(chatId));

        testStateService.reset(chatId);

        assertNull(testStateService.get(chatId));
    }

    @Test
    void testReset_NonExistentState() {
        Long chatId = 12345L;

        // Should not throw exception
        assertDoesNotThrow(() -> testStateService.reset(chatId));
    }

    @Test
    void testClearAll() {
        Long chatId1 = 12345L;
        Long chatId2 = 67890L;

        testStateService.getOrCreate(chatId1);
        testStateService.getOrCreate(chatId2);

        assertNotNull(testStateService.get(chatId1));
        assertNotNull(testStateService.get(chatId2));

        testStateService.clearAll();

        assertNull(testStateService.get(chatId1));
        assertNull(testStateService.get(chatId2));
    }

    @Test
    void testPut() {
        Long chatId = 12345L;
        TestState newState = new TestState();
        newState.setCurrentTopicIndex(5);
        newState.setCurrentQuestionIndex(2);

        testStateService.put(chatId, newState);

        TestState retrievedState = testStateService.get(chatId);
        assertNotNull(retrievedState);
        assertEquals(5, retrievedState.getCurrentTopicIndex());
        assertEquals(2, retrievedState.getCurrentQuestionIndex());
    }

    @Test
    void testPut_OverwritesExisting() {
        Long chatId = 12345L;
        TestState state1 = testStateService.getOrCreate(chatId);
        state1.setCurrentTopicIndex(1);

        TestState state2 = new TestState();
        state2.setCurrentTopicIndex(10);

        testStateService.put(chatId, state2);

        TestState retrievedState = testStateService.get(chatId);
        assertEquals(10, retrievedState.getCurrentTopicIndex());
    }
}

