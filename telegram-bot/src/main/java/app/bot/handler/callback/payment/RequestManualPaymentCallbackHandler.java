package app.bot.handler.callback.payment;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.facade.AnalyticsFacade;
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
public class RequestManualPaymentCallbackHandler implements CallbackHandler {

  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;
  private final BotTextService textService;



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

      String payload = "program_access_" + chatId;
      analytics.trackPaymentStart(chatId, payload, 100, "USD");

      return new TextResponse(chatId, textService.format(TextMarker.MANUAL_PAYMENT),
          KeyboardFactory.from(List.of(
              new KeyboardOption("Я оплатил", TextMarker.SEND_PAYMENT_REQUEST),
              new KeyboardOption("Вернуться к описанию.", TextMarker.INFO_PROGRAM))));
    }
    return new TextResponse(chatId, "Сейчас оплата не доступна. Попробуйте вызвать меню", null);
  }
}