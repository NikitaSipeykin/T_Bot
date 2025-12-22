package app.bot.bot;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

public interface BotHandler {

  BotApiMethod<?> handle();
}
