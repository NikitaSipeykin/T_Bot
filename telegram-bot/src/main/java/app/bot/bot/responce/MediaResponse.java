package app.bot.bot.responce;

public record MediaResponse(
    Long chatId,
    MediaType type,
    String fileId
) implements BotResponse {}

