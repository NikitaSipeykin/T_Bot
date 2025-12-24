package app.bot.handler.callback.test;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.CompositeResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.broadcast.SubscriberService;
import app.core.test.OutgoingMessage;
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
public class StartTestCallbackHandler implements CallbackHandler {

  private final TestService testService;
  private final BotTextService textService;
  private final UserStateService userStateService;
  private final SubscriberService subscriberService;

  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.CHAKRA_INTRO);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.DEFAULT);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    if (!subscriberService.isFinishedTesting(chatId)) {
      Object response = testService.startTest(chatId);

      compositeResponse.responses().add(new TextResponse(chatId, textService.get(TextMarker.CHAKRA_INTRO), null));
      if (response instanceof OutgoingMessage m) {
        compositeResponse.responses().add(new TextResponse(chatId, m.text(), KeyboardFactory.toKeyboard(m.options())));
        return compositeResponse;
      }
    }

    return new TextResponse(chatId, textService.get(TextMarker.TEST_END_ALREADY),
        KeyboardFactory.from(List.of(new KeyboardOption("Хочу больше!", TextMarker.VIBRATIONS_AND_CHAKRAS))));
  }
}
