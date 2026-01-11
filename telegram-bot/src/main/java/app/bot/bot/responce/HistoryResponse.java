package app.bot.bot.responce;

public record HistoryResponse(
    Long chatId
) implements BotResponse {}