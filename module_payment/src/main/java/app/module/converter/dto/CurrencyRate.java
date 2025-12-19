package app.module.converter.dto;

public record CurrencyRate(
    String from,
    String to,
    int rate // ×100
) {}