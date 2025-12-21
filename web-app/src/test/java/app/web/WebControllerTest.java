package app.web;

import app.core.broadcast.BroadcastService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WebControllerTest {

    @Mock
    private BroadcastService broadcastService;

    @InjectMocks
    private WebController webController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(webController).build();
    }

    @Test
    void testIndex() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"));
    }

    @Test
    void testTextsPage() throws Exception {
        mockMvc.perform(get("/texts"))
            .andExpect(status().isOk())
            .andExpect(view().name("edit-texts.html"));
    }

    @Test
    void testBroadcastPage() throws Exception {
        mockMvc.perform(get("/broadcast"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"));
    }

    @Test
    void testMediaPage() throws Exception {
        mockMvc.perform(get("/media"))
            .andExpect(status().isOk())
            .andExpect(view().name("upload-videos.html"));
    }

    @Test
    void testSend() throws Exception {
        String requestBody = "{\"text\":\"Test broadcast message\"}";

        doNothing().when(broadcastService).broadcast(anyString());

        mockMvc.perform(post("/send")
                .contentType("application/json")
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content().string("ok"));

        // Verify that broadcast was called in a separate thread
        // Note: The actual broadcast happens in a new thread, so we verify with a delay
        Thread.sleep(100);
        verify(broadcastService, timeout(1000).atLeastOnce()).broadcast("Test broadcast message");
    }

    @Test
    void testSend_WithEmptyText() throws Exception {
        String requestBody = "{\"text\":\"\"}";

        doNothing().when(broadcastService).broadcast(anyString());

        mockMvc.perform(post("/send")
                .contentType("application/json")
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content().string("ok"));

        Thread.sleep(100);
        verify(broadcastService, timeout(1000).atLeastOnce()).broadcast("");
    }
}


