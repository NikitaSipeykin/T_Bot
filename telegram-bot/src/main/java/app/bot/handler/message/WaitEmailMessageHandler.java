package app.bot.handler.message;

import app.bot.bot.responce.BotResponse;
import app.bot.email.EmailService;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.broadcast.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class WaitEmailMessageHandler implements MessageHandler {

  private final EmailService emailService;
  private final SubscriberService subscriberService;
  private final UserStateService userStateService;

  @Override
  public UserState supports() {
    return UserState.WAIT_EMAIL;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    String input = message.getText();

    String code = subscriberService.getCode(chatId);

//    if (!input.equals(code)) {
//      return SendMessage.builder()
//          .chatId(chatId.toString())
//          .text("Неверный код! Попробуй снова.")
//          .build();
//    }
//
//    subscriberService.setVerified(chatId);
//    userStateService.setState(chatId, UserState.RESULT);
//
//    return SendMessage.builder()
//        .chatId(chatId.toString())
//        .text("Отлично! Вот твой подарок 🎁")
//        .build();

    //Todo: dummy
    return null;
  }
}
