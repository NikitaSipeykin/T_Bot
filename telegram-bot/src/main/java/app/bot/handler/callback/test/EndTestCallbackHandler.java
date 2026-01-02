package app.bot.handler.callback.test;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.CompositeResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.facade.AnalyticsFacade;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EndTestCallbackHandler implements CallbackHandler {
  private final TestService testService;
  private final BotTextService textService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.TEST_END);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    analytics.trackButtonClick(query, TextMarker.TEST_END);

    userStateService.setState(chatId, UserState.DEFAULT);
    
    List<String> topics = testService.getResultTopics(chatId);
    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    log.info("topics = " + topics);

    for (String topic : topics) {
      log.info("topic = " + topic);
      compositeResponse.responses().add(new TextResponse(chatId, textService.format(topic + "_PRESENT"), null));
    }
    analytics.trackCtaShown(chatId, TextMarker.VIBRATIONS_AND_CHAKRAS);
    compositeResponse.responses().add(new TextResponse(chatId, textService.format(TextMarker.PRESENT_END),
        KeyboardFactory.from(List.of(new KeyboardOption("Хочу больше!", TextMarker.VIBRATIONS_AND_CHAKRAS)))));

    return compositeResponse;
  }
}
