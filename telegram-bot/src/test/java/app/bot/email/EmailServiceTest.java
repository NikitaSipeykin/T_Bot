package app.bot.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // Setup is handled by Mockito
    }

    @Test
    void testSendVerificationCode() {
        String email = "test@example.com";
        String code = "123456";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendVerificationCode(email, code);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(email, sentMessage.getTo()[0]);
        assertEquals("Ваш код подтверждения", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains(code));
    }

    @Test
    void testIsValidEmail_ValidEmails() {
        assertTrue(emailService.isValidEmail("test@example.com"));
        assertTrue(emailService.isValidEmail("user.name@domain.co.uk"));
        assertTrue(emailService.isValidEmail("user+tag@example.com"));
        assertTrue(emailService.isValidEmail("user123@test-domain.com"));
    }

    @Test
    void testIsValidEmail_InvalidEmails() {
        assertFalse(emailService.isValidEmail("invalid"));
        assertFalse(emailService.isValidEmail("invalid@"));
        assertFalse(emailService.isValidEmail("@example.com"));
        assertFalse(emailService.isValidEmail(""));
        assertFalse(emailService.isValidEmail(null));
    }

    @Test
    void testGenerateCode() {
        String code1 = emailService.generateCode();
        String code2 = emailService.generateCode();

        assertNotNull(code1);
        assertNotNull(code2);
        assertEquals(6, code1.length());
        assertEquals(6, code2.length());
        assertTrue(code1.matches("\\d{6}"));
        assertTrue(code2.matches("\\d{6}"));
        // Codes should be different (very high probability)
        // Note: There's a tiny chance they could be the same, but it's extremely unlikely
    }

    @Test
    void testGenerateCode_Format() {
        for (int i = 0; i < 10; i++) {
            String code = emailService.generateCode();
            assertTrue(code.matches("\\d{6}"), "Code should be 6 digits: " + code);
            int codeValue = Integer.parseInt(code);
            assertTrue(codeValue >= 100000 && codeValue <= 999999, "Code should be between 100000 and 999999");
        }
    }
}

