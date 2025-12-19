package app.module.payment;

import app.core.payment.AccessService;
import app.core.payment.PaidPaymentInfo;
import app.core.payment.PaymentCommand;
import app.core.payment.PaymentResult;
import app.module.payment.dao.Payment;
import app.module.payment.dao.PaymentStatus;
import app.module.payment.repo.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCommandHandlerTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AccessService accessService;

    @InjectMocks
    private PaymentCommandHandler paymentCommandHandler;

    private Payment payment;
    private PaymentCommand paymentCommand;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId(1L);
        payment.setChatId(12345L);
        payment.setPayload("test_payload_123");
        payment.setAmount(10000);
        payment.setCurrency("USD");
        payment.setStatus(PaymentStatus.CREATED);

        paymentCommand = new PaymentCommand(
            12345L,
            12345L,
            "test_payload_123",
            10000,
            "USD",
            "provider_charge_123"
        );
    }

    @Test
    void testCreatePayment_NewPayment() {
        Long chatId = 12345L;
        String payload = "test_payload_123";
        int amount = 10000;
        String currency = "USD";

        when(paymentRepository.existsByPayload(payload)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentCommandHandler.createPayment(chatId, payload, amount, currency);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();
        assertEquals(chatId, savedPayment.getChatId());
        assertEquals(payload, savedPayment.getPayload());
        assertEquals(amount, savedPayment.getAmount());
        assertEquals(currency, savedPayment.getCurrency());
        assertEquals(PaymentStatus.CREATED, savedPayment.getStatus());
    }

    @Test
    void testCreatePayment_DuplicatePayload() {
        Long chatId = 12345L;
        String payload = "test_payload_123";
        int amount = 10000;
        String currency = "USD";

        when(paymentRepository.existsByPayload(payload)).thenReturn(true);

        paymentCommandHandler.createPayment(chatId, payload, amount, currency);

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testHandlePayment_Success() {
        payment.setStatus(PaymentStatus.CREATED);

        when(paymentRepository.existsByProviderPaymentId("provider_charge_123")).thenReturn(false);
        when(paymentRepository.findByPayload("test_payload_123")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(accessService).grantAccess(any(PaidPaymentInfo.class));

        PaymentResult result = paymentCommandHandler.handlePayment(paymentCommand);

        assertTrue(result.success());
        assertEquals("Access granted", result.message());

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();
        assertEquals(PaymentStatus.PAID, savedPayment.getStatus());
        assertNotNull(savedPayment.getPaidAt());
        assertEquals("provider_charge_123", savedPayment.getProviderPaymentId());

        ArgumentCaptor<PaidPaymentInfo> infoCaptor = ArgumentCaptor.forClass(PaidPaymentInfo.class);
        verify(accessService, times(1)).grantAccess(infoCaptor.capture());

        PaidPaymentInfo paidInfo = infoCaptor.getValue();
        assertEquals(1L, paidInfo.paymentId());
        assertEquals(12345L, paidInfo.chatId());
        assertNotNull(paidInfo.paidAt());
    }

    @Test
    void testHandlePayment_AlreadyProcessedByProviderId() {
        when(paymentRepository.existsByProviderPaymentId("provider_charge_123")).thenReturn(true);

        PaymentResult result = paymentCommandHandler.handlePayment(paymentCommand);

        assertTrue(result.success());
        assertEquals("Payment already processed", result.message());
        verify(paymentRepository, never()).findByPayload(anyString());
        verify(accessService, never()).grantAccess(any());
    }

    @Test
    void testHandlePayment_AlreadyPaid() {
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        when(paymentRepository.existsByProviderPaymentId("provider_charge_123")).thenReturn(false);
        when(paymentRepository.findByPayload("test_payload_123")).thenReturn(Optional.of(payment));

        PaymentResult result = paymentCommandHandler.handlePayment(paymentCommand);

        assertTrue(result.success());
        assertEquals("Payment already processed", result.message());
        verify(accessService, never()).grantAccess(any());
    }

    @Test
    void testHandlePayment_PaymentNotFound() {
        when(paymentRepository.existsByProviderPaymentId("provider_charge_123")).thenReturn(false);
        when(paymentRepository.findByPayload("test_payload_123")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> {
            paymentCommandHandler.handlePayment(paymentCommand);
        });

        verify(accessService, never()).grantAccess(any());
    }
}

