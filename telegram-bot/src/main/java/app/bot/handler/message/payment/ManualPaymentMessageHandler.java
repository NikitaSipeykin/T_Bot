package app.bot.handler.message.payment;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.message.MessageHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ManualPaymentMessageHandler implements MessageHandler {

  private final BotTextService textService;
  private final AnalyticsFacade analyticsFacade;


  @Override
  public UserState supports() {
    return UserState.MANUAL_PAYMENT;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();

    return new TextResponse(chatId, "Дождитесь ответа от админестратора!", null);
  }
}
