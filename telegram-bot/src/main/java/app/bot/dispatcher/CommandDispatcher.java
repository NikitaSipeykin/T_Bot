package app.bot.dispatcher;

import app.bot.bot.responce.BotResponse;
import app.bot.handler.command.CommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CommandDispatcher {

  private final Map<String, CommandHandler> handlers;

  public CommandDispatcher(List<CommandHandler> handlers) {
    this.handlers = handlers.stream()
        .collect(Collectors.toMap(CommandHandler::command, h -> h));
  }

  public BotResponse dispatch(Message message) {
    String text = message.getText();

    log.info("text = " + text);
    if (text == null || !text.startsWith("/")) {
      return null;
    }

    String command = text.contains(" ") ? text.substring(0, text.indexOf(" ")) : text;

    CommandHandler handler = handlers.get(command);
    if (handler == null) {
      return null;
    }

    return handler.handle(message);
  }
}
