package app.bot.bot.responce;

public sealed interface BotResponse
    permits TextResponse, MediaResponse, CompositeResponse {
}

