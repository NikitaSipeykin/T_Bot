package app.core;

public record DailyUpdateResult(
    Long chatId,
    String newBlockName
) {}

