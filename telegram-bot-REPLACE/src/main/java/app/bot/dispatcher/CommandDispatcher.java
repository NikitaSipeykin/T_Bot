package app.bot.dispatcher;

import app.bot.bot.responce.BotResponse;
import app.bot.handler.command.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CommandDispatcher {

  private final Map<String, CommandHandler> handlers;

  public CommandDispatcher(List<CommandHandler> handlers) {
    this.handlers = handlers.stream()
        .collect(Collectors.toMap(CommandHandler::command, h -> h));
  }

  public BotResponse dispatch(Message message) {
    String text = message.getText();

    if (text == null || !text.startsWith("/")) {
      return null;
    }

    CommandHandler handler = handlers.get(text);
    if (handler == null) {
      return null;
    }

    return handler.handle(message);
  }
}
