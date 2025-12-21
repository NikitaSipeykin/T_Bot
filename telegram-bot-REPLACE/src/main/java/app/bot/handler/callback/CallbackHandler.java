package app.bot.handler.callback;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackHandler {

  boolean supports(String callbackData);

  BotApiMethod<?> handle(CallbackQuery callbackQuery);
}
