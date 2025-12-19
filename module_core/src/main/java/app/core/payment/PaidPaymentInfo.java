package app.core.payment;


import java.time.LocalDateTime;

public record PaidPaymentInfo(
    Long paymentId,
    Long chatId,
    LocalDateTime paidAt
) {}
