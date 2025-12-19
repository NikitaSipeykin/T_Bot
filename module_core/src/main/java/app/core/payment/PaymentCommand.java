package app.core.payment;

public record PaymentCommand(
    long telegramUserId,
    long chatId,
    String payload,
    int amount,
    String currency,
    String providerChargeId
) {}

