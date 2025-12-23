package app.bot.bot.responce;

public sealed interface BotResponse
    permits CompositeResponse, MediaResponse, SendInvoiceResponse, SendWithDelayedResponse, TextResponse {
}

