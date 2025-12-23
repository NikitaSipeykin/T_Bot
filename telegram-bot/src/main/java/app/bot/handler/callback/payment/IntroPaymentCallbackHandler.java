package app.bot.handler.callback.payment;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserStateService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IntroPaymentCallbackHandler implements CallbackHandler {

  private final BotTextService textService;
  private final UserStateService userStateService;

  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.VIBRATIONS_AND_CHAKRAS);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();

    return new TextResponse(chatId, textService.get(TextMarker.VIBRATIONS_AND_CHAKRAS),
        KeyboardFactory.from(List.of(
            new KeyboardOption("Да, записаться!", TextMarker.PAYMENT),
            new KeyboardOption("Расскажи подробнее", TextMarker.INFO_PROGRAM))));
  }
}
