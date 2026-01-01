package app.bot.handler.callback.payment;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentCallbackHandler implements CallbackHandler {

  private final UserStateService userStateService;

  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.PAYMENT);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    if (userStateService.getState(chatId).equals(UserState.PAYMENT) ||
        userStateService.getState(chatId).equals(UserState.NEED_PAYMENT)) {
      userStateService.setState(chatId, UserState.PAYMENT);

      return new TextResponse(chatId, "Выберите валюту для оплаты",
          KeyboardFactory.from(List.of(
              new KeyboardOption("UZS", "UZS"),
              new KeyboardOption("USD", "USD"),
              new KeyboardOption("EUR", "EUR"),
              new KeyboardOption("RUB", "RUB"))));
    }
    return new TextResponse(chatId, "Сейчас оплата не доступна. Попробуйте вызвать меню", null);
  }
}
