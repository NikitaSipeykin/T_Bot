package app.bot.handler.message;

import app.bot.bot.responce.BotResponse;
import app.bot.state.UserState;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageHandler {

  UserState supports();

  BotResponse handle(Message message);
}

