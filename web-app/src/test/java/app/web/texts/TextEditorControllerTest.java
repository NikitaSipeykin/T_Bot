package app.web.texts;

import app.module.node.texts.BotText;
import app.module.node.texts.BotTextRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TextEditorControllerTest {

    @Mock
    private BotTextRepository repository;

    @Mock
    private TextSyncService syncService;

    @InjectMocks
    private TextEditorController textEditorController;

    private MockMvc mockMvc;
    private BotText botText;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(textEditorController).build();

        botText = new BotText();
        botText.setId("TEST_TEXT");
        botText.setValue("Original value");
    }

    @Test
    void testGetAll() throws Exception {
        List<BotText> texts = Arrays.asList(
            botText,
            createBotText("TEXT_2", "Value 2"),
            createBotText("TEXT_3", "Value 3")
        );

        when(repository.findAll()).thenReturn(texts);

        mockMvc.perform(get("/api/texts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(3));

        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetAll_EmptyList() throws Exception {
        when(repository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/texts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));

        verify(repository, times(1)).findAll();
    }

    @Test
    void testUpdate_Success() throws Exception {
        BotText updated = new BotText();
        updated.setValue("Updated value");

        when(repository.findById("TEST_TEXT")).thenReturn(Optional.of(botText));
        when(repository.save(any(BotText.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(syncService).sendToBot(any(BotText.class));

        mockMvc.perform(put("/api/texts/TEST_TEXT")
                .contentType("application/json")
                .content("{\"value\":\"Updated value\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("TEST_TEXT"))
            .andExpect(jsonPath("$.value").value("Updated value"));

        verify(repository, times(1)).findById("TEST_TEXT");
        verify(repository, times(1)).save(botText);
        verify(syncService, times(1)).sendToBot(botText);
        assertEquals("Updated value", botText.getValue());
    }

    @Test
    void testUpdate_NotFound() {
        BotText updated = new BotText();
        updated.setValue("Updated value");
        
        when(repository.findById("NON_EXISTENT")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            textEditorController.update("NON_EXISTENT", updated);
        });

        assertEquals("Not found", exception.getMessage());
        verify(repository, times(1)).findById("NON_EXISTENT");
        verify(repository, never()).save(any());
        verify(syncService, never()).sendToBot(any());
    }

    @Test
    void testUpdate_SyncsToBot() {
        BotText updated = new BotText();
        updated.setValue("New value");

        when(repository.findById("TEST_TEXT")).thenReturn(Optional.of(botText));
        when(repository.save(any(BotText.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(syncService).sendToBot(any(BotText.class));

        BotText result = textEditorController.update("TEST_TEXT", updated);

        assertNotNull(result);
        assertEquals("New value", result.getValue());
        verify(syncService, times(1)).sendToBot(botText);
    }

    private BotText createBotText(String id, String value) {
        BotText text = new BotText();
        text.setId(id);
        text.setValue(value);
        return text;
    }
}

