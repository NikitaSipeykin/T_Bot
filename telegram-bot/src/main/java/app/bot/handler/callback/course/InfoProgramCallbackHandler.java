package app.bot.handler.callback.course;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.broadcast.SubscriberService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.test.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InfoProgramCallbackHandler implements CallbackHandler {
  private final BotTextService textService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.INFO_PROGRAM);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.NEED_PAYMENT);
    analytics.trackButtonClick(query, TextMarker.INFO_PROGRAM);
    analytics.trackBlockView(chatId, TextMarker.INFO_PROGRAM);
    analytics.trackCtaShown(chatId, TextMarker.PAYMENT);

    return new TextResponse(chatId, textService.format(TextMarker.INFO_PROGRAM),
        KeyboardFactory.from(List.of(new KeyboardOption("Записаться на курс", TextMarker.PAYMENT))));
  }
}
