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
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IntroPaymentCallbackHandler implements CallbackHandler {

  private final BotTextService textService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.VIBRATIONS_AND_CHAKRAS);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    analytics.trackButtonClick(query, TextMarker.VIBRATIONS_AND_CHAKRAS);

    userStateService.setState(chatId, UserState.NEED_PAYMENT);

    analytics.trackBlockView(
        chatId,
        TextMarker.VIBRATIONS_AND_CHAKRAS,
        Map.of(
            "state", UserState.NEED_PAYMENT.name(),
            "source", "callback"
        )
    );

    return new TextResponse(chatId, textService.get(TextMarker.VIBRATIONS_AND_CHAKRAS),
        KeyboardFactory.from(List.of(
            new KeyboardOption("Да, записаться!", TextMarker.PAYMENT),
            new KeyboardOption("Расскажи подробнее", TextMarker.INFO_PROGRAM))));
  }
}
