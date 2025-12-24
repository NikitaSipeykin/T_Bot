package app.bot.handler.message.mail;

import app.bot.bot.responce.BotResponse;
import app.bot.email.EmailService;
import app.bot.handler.message.MessageHandler;
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
public class MailRequestMessageHandler implements MessageHandler {

  private final EmailService emailService;
  private final SubscriberService subscriberService;
  private final UserStateService userStateService;

  @Override
  public UserState supports() {
    return UserState.MAIL_REQUEST;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    String email = message.getText();

//    if (!emailService.isValidEmail(email)) {
//      return SendMessage.builder()
//          .chatId(chatId.toString())
//          .text("Кажется, это не почта. Попробуй снова:")
//          .build();
//    }
//
//    String code = emailService.generateCode();
//    subscriberService.setEmail(chatId, email);
//    subscriberService.setCode(chatId, code);
//
//    emailService.sendVerificationCode(email, code);
//    userStateService.setState(chatId, UserState.WAIT_EMAIL);
//
//    return SendMessage.builder()
//        .chatId(chatId.toString())
//        .text("Я отправил код на почту. Введите его:")
//        .build();
    //Todo: dummy
    return null;
  }
}
