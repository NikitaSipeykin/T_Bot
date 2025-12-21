package app.bot.sender;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

public interface TelegramExecutor {
  void execute(BotApiMethod<?> method);
}
