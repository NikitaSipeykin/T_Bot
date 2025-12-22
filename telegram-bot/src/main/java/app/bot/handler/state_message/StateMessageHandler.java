package app.bot.handler.state_message;


import app.bot.state.UserState;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface StateMessageHandler {

  UserState state();

  void handle(Update update);
}
