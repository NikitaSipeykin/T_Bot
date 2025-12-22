package app.bot.bot.responce;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public record TextResponse(
    Long chatId,
    String text,
    InlineKeyboardMarkup keyboard
) implements BotResponse {}

