package app.bot.handler.callback;

import app.bot.state.UserStateService;
import app.core.test.FinalMessage;
import app.core.test.OutgoingMessage;
import app.module.node.texts.BotTextService;
import app.module.test.TestService;
import app.bot.state.UserState;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import java.util.List;


@Component
@RequiredArgsConstructor
public class TestCallbackHandler implements CallbackHandler {

  private final TestService testService;
  private final BotTextService textService;
  private final UserStateService userStateService;

  @Override
  public boolean supports(String callbackData) {
    return callbackData.startsWith("TEST_Q_");
  }

  @Override
  public BotApiMethod<?> handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    String data = query.getData();

    Object response = testService.processAnswer(chatId, data);

    if (response instanceof OutgoingMessage m) {
      return SendMessage.builder()
          .chatId(chatId.toString())
          .text(m.text())
          .replyMarkup(KeyboardFactory.from(
              m.options().stream()
                  .map(o -> new KeyboardOption(o.getText(), o.getCallback()))
                  .toList()
          ))
          .build();
    }

    if (response instanceof FinalMessage f) {
      userStateService.setState(chatId, UserState.RESULT);

      return SendMessage.builder()
          .chatId(chatId.toString())
          .text(f.text())
          .build();
    }

    throw new IllegalStateException(
        "Unsupported response type: " + response.getClass()
    );
  }
}

