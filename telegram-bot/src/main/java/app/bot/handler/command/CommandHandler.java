package app.bot.handler.command;

import app.bot.bot.responce.BotResponse;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface CommandHandler {

  String command();

  BotResponse handle(Message message);
}
