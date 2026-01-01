package app.bot.bot.responce;

import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;

public record SendInvoiceResponse(SendInvoice response) implements BotResponse {}