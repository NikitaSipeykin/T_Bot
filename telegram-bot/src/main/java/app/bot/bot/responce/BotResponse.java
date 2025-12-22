package app.bot.bot.responce;

public sealed interface BotResponse
    permits CompositeResponse, MediaResponse, SendWithDelayedResponse, TextResponse {
}

