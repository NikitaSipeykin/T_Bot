package app.bot.handler.callback;

import app.bot.bot.responce.BotResponse;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackHandler {

  boolean supports(String callbackData);

  BotResponse handle(CallbackQuery callbackQuery);
}
