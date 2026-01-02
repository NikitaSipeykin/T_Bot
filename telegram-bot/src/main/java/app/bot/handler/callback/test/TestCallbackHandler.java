package app.bot.handler.callback.test;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.CompositeResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.callback.CallbackHandler;
import app.bot.state.UserStateService;
import app.core.broadcast.SubscriberService;
import app.core.test.FinalMessage;
import app.core.test.OutgoingMessage;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.test.TestService;
import app.bot.state.UserState;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
@RequiredArgsConstructor
public class TestCallbackHandler implements CallbackHandler {

  private final TestService testService;
  private final BotTextService textService;
  private final SubscriberService subscriberService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.startsWith("TEST_Q_");
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    String data = query.getData();

    analytics.trackButtonClick(query, data);
    analytics.trackTestAnswer(chatId, data);

    userStateService.setState(chatId, UserState.DEFAULT);

    Object response = testService.processAnswer(chatId, data);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    if (response instanceof OutgoingMessage m) {
      if (m.isNextTopic()) {
        compositeResponse.responses().add(new TextResponse(chatId, textService.get(TextMarker.GOT_YOU), null));
      }
      analytics.trackTestQuestionShown(chatId, m.text().substring(0, 25));
      compositeResponse.responses().add(new TextResponse(chatId, m.text(), KeyboardFactory.toKeyboard(m.options())));
    }else if (response instanceof FinalMessage f) {

      analytics.trackTestFinish(chatId, f.recommendedTopicNames().size());

      compositeResponse.responses().add(new TextResponse(chatId, f.text(), null));
      testService.saveResultTopics(chatId, f.recommendedTopicNames());

      if (Objects.equals(f.text(), textService.format(TextMarker.ALL_ZERO))) {
        analytics.trackZeroResult(chatId);

        compositeResponse.responses().add(new TextResponse(chatId, textService.get(TextMarker.ALL_ZERO_RESULT),
            KeyboardFactory.from(List.of(new KeyboardOption("Хорошо!", TextMarker.VIBRATIONS_AND_CHAKRAS),
                new KeyboardOption("Попробовать еще раз!", TextMarker.CHAKRA_INTRO)))));

        return compositeResponse;
      }

      subscriberService.setFinishedTest(chatId);
      compositeResponse.responses().add(new TextResponse(chatId, textService.format(TextMarker.RESULT),
          KeyboardFactory.from(List.of(new KeyboardOption("Хочу решения!", TextMarker.TEST_END)))));
    }

    return compositeResponse;
  }
}

