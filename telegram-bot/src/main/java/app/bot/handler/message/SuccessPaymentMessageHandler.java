package app.bot.handler.message;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
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
public class SuccessPaymentMessageHandler implements MessageHandler {

  private final BotTextService textService;

  @Override
  public UserState supports() {
    return UserState.PAYMENT;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();

    return new TextResponse(chatId, "✅ Оплата прошла успешно! Добро пожаловать в программу",
        KeyboardFactory.from(List.of(new KeyboardOption("Начать", TextMarker.PROGRAM))));
  }
}