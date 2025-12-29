package app.bot.handler.message.payment;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.handler.message.MessageHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
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
  private final UserStateService userStateService;

  @Override
  public UserState supports() {
    return UserState.SUCCESS_PAYMENT;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    userStateService.setState(chatId, UserState.COURSE);

    return new TextResponse(chatId, "✅ Оплата прошла успешно! Добро пожаловать в программу",
        KeyboardFactory.from(List.of(new KeyboardOption("Начать", TextMarker.PROGRAM))));
  }
}