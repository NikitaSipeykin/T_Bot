package app.bot.handler.message.payment;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
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

@Component
@RequiredArgsConstructor
public class NeedPaymentMessageHandler implements MessageHandler {

  private final BotTextService textService;

  @Override
  public UserState supports() {
    return UserState.NEED_PAYMENT;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    return new TextResponse(chatId, textService.get(TextMarker.NEED_PAYMENT),
        KeyboardFactory.from(List.of(
            new KeyboardOption("Да, записаться!", TextMarker.PAYMENT),
            new KeyboardOption("Расскажи подробнее", TextMarker.INFO_PROGRAM))));
  }
}
