package app.bot.handler.callback.program;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserStateService;
import app.core.broadcast.SubscriberService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.test.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InfoProgramCallbackHandler implements CallbackHandler {
  private final TestService testService;
  private final BotTextService textService;
  private final UserStateService userStateService;
  private final SubscriberService subscriberService;

  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.INFO_PROGRAM);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();

    return new TextResponse(chatId, textService.format(TextMarker.INFO_PROGRAM),
        KeyboardFactory.from(List.of(new KeyboardOption("Записаться на курс", TextMarker.PAYMENT))));
  }
}
