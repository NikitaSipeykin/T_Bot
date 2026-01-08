package app.bot.dispatcher;

import app.bot.bot.responce.BotResponse;
import app.bot.handler.callback.CallbackHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Slf4j
@Component
public class CallbackDispatcher {

  private final List<CallbackHandler> handlers;

  public CallbackDispatcher(List<CallbackHandler> handlers) {
    this.handlers = handlers;
  }

  public BotResponse dispatch(CallbackQuery query) {
    String data = query.getData();

    log.debug("Callback data = " + data);

    CallbackHandler handler = handlers.stream()
        .filter(h -> h.supports(data))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "No CallbackHandler found for callbackData: " + data
        ));

    log.debug("Callback [{}] handled by {}", data, handler.getClass().getSimpleName());

    return handler.handle(query);
  }
}
