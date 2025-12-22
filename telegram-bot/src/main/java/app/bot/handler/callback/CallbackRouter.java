package app.bot.handler.callback;

import app.bot.bot.responce.BotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CallbackRouter {

  private final List<CallbackHandler> handlers;

  public BotResponse route(CallbackQuery callbackQuery) {
    String data = callbackQuery.getData();

    return handlers.stream()
        .filter(h -> h.supports(data))
        .findFirst()
        .map(h -> h.handle(callbackQuery))
        .orElse(null);
  }
}
