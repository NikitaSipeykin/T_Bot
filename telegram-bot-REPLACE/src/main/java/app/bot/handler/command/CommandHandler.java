package app.bot.handler.command;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface CommandHandler {

  String command();

  BotApiMethod<?> handle(Message message);
}
