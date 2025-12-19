package app.module.payment;

import app.core.payment.PaidPaymentInfo;
import app.module.payment.dao.Payment;
import app.module.payment.dao.PaymentStatus;
import app.module.payment.repo.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentQueryServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentQueryServiceImpl paymentQueryService;

    private Payment paidPayment;
    private Payment unpaidPayment;
    private Payment createdPayment;

    @BeforeEach
    void setUp() {
        paidPayment = new Payment();
        paidPayment.setId(1L);
        paidPayment.setChatId(12345L);
        paidPayment.setStatus(PaymentStatus.PAID);
        paidPayment.setPaidAt(LocalDateTime.now());

        unpaidPayment = new Payment();
        unpaidPayment.setId(2L);
        unpaidPayment.setChatId(67890L);
        unpaidPayment.setStatus(PaymentStatus.CREATED);
        unpaidPayment.setPaidAt(null);

        createdPayment = new Payment();
        createdPayment.setId(3L);
        createdPayment.setChatId(11111L);
        createdPayment.setStatus(PaymentStatus.CREATED);
        createdPayment.setPaidAt(LocalDateTime.now());
    }

    @Test
    void testGetPaidPayment_Success() {
        Long paymentId = 1L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(paidPayment));

        Optional<PaidPaymentInfo> result = paymentQueryService.getPaidPayment(paymentId);

        assertTrue(result.isPresent());
        PaidPaymentInfo info = result.get();
        assertEquals(1L, info.paymentId());
        assertEquals(12345L, info.chatId());
        assertNotNull(info.paidAt());
    }

    @Test
    void testGetPaidPayment_UnpaidPayment() {
        Long paymentId = 2L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(unpaidPayment));

        Optional<PaidPaymentInfo> result = paymentQueryService.getPaidPayment(paymentId);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetPaidPayment_PaymentNotFound() {
        Long paymentId = 999L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        Optional<PaidPaymentInfo> result = paymentQueryService.getPaidPayment(paymentId);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetPaidPayment_CreatedStatusWithPaidAt() {
        Long paymentId = 3L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(createdPayment));

        Optional<PaidPaymentInfo> result = paymentQueryService.getPaidPayment(paymentId);

        // Should not return because status is CREATED, not PAID
        assertFalse(result.isPresent());
    }

    @Test
    void testGetPaidPayment_PaidStatusWithoutPaidAt() {
        Payment paymentWithoutPaidAt = new Payment();
        paymentWithoutPaidAt.setId(4L);
        paymentWithoutPaidAt.setChatId(22222L);
        paymentWithoutPaidAt.setStatus(PaymentStatus.PAID);
        paymentWithoutPaidAt.setPaidAt(null);

        when(paymentRepository.findById(4L)).thenReturn(Optional.of(paymentWithoutPaidAt));

        Optional<PaidPaymentInfo> result = paymentQueryService.getPaidPayment(4L);

        // Should not return because paidAt is null
        assertFalse(result.isPresent());
    }
}

