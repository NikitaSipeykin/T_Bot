package app.bot.handler.message.payment;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.handler.message.MessageHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.facade.AnalyticsFacade;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StepBackMessageHandler implements MessageHandler {

  private final AnalyticsFacade analyticsFacade;

  @Override
  public UserState supports() {
    return UserState.PAYMENT;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();

    analyticsFacade.trackBlockView(chatId, "PAYMENT_STEP_BACK", Map.of("source", "payment_flow"));

    analyticsFacade.trackCtaShown(chatId, TextMarker.VIBRATIONS_AND_CHAKRAS);

    return new TextResponse(chatId, "Хотите вернуться к описанию курса?",
        KeyboardFactory.from(List.of(
            new KeyboardOption("Да", TextMarker.VIBRATIONS_AND_CHAKRAS))));
  }
}
