package app.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BotPropertiesTest {

    private BotProperties botProperties;

    @BeforeEach
    void setUp() {
        botProperties = new BotProperties();
    }

    @Test
    void testGettersAndSetters() {
        String token = "test_token_123";
        String username = "test_bot";
        Long adminId = 12345L;
        String providerToken = "provider_token";
        String currency = "USD";
        boolean testMode = true;

        botProperties.setToken(token);
        botProperties.setUsername(username);
        botProperties.setAdminId(adminId);
        botProperties.setProviderToken(providerToken);
        botProperties.setCurrency(currency);
        botProperties.setTestMode(testMode);

        assertEquals(token, botProperties.getToken());
        assertEquals(username, botProperties.getUsername());
        assertEquals(adminId, botProperties.getAdminId());
        assertEquals(providerToken, botProperties.getProviderToken());
        assertEquals(currency, botProperties.getCurrency());
        assertEquals(testMode, botProperties.isTestMode());
    }

    @Test
    void testDefaultValues() {
        assertNull(botProperties.getToken());
        assertNull(botProperties.getUsername());
        assertNull(botProperties.getAdminId());
        assertNull(botProperties.getProviderToken());
        assertNull(botProperties.getCurrency());
        assertFalse(botProperties.isTestMode());
    }

    @Test
    void testSetTestMode_False() {
        botProperties.setTestMode(false);
        assertFalse(botProperties.isTestMode());
    }

    @Test
    void testSetAdminId_Null() {
        botProperties.setAdminId(null);
        assertNull(botProperties.getAdminId());
    }
}

