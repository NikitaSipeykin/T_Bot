package app.core.payment;

public record PaymentResult(
    boolean success,
    String message
) {}
