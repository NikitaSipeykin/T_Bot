package app.bot.bot.responce;

public sealed interface BotResponse
    permits CompositeResponse, HistoryResponse, MediaResponse, SendInvoiceResponse, SendWithDelayedResponse, TextResponse {
}

