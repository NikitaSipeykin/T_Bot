package app.module.payment.props;

public record PaymentOption(
    String providerKey,
    String providerToken,
    String currency,
    int price
) {}

