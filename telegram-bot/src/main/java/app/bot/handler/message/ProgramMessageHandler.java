package app.bot.handler.message;

import app.bot.bot.responce.BotResponse;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.program.ProgramMessage;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.program.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProgramMessageHandler implements MessageHandler {

  private final ProgramService programService;
  private final BotTextService textService;
  private final UserStateService userStateService;

  @Override
  public UserState supports() {
    return UserState.PROGRAM;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();

    //Todo: dummy
//    if (!programService.checkUserAccessProgram(chatId)) {
//      userStateService.setState(chatId, UserState.NEED_PAYMENT);
//
//      return SendMessage.builder()
//          .chatId(chatId.toString())
//          .text(textService.format(TextMarker.NEED_PAYMENT))
//          .replyMarkup(KeyboardFactory.from(List.of(
//              new KeyboardOption("Да, записаться!", TextMarker.PAYMENT),
//              new KeyboardOption("Расскажи подробнее", TextMarker.INFO_PROGRAM)
//          )))
//          .build();
//    }
//
//    Object response = programService.nextMessage(chatId);
//
//    if (response instanceof ProgramMessage m) {
//
//      userStateService.setState(chatId, UserState.PROGRAM);
//
//      if (m.options().isEmpty()) {
//        return SendMessage.builder()
//            .chatId(chatId.toString())
//            .text(textService.format(m.text()))
//            .build();
//      }
//
//      return SendMessage.builder()
//          .chatId(chatId.toString())
//          .text(textService.format(m.text()))
//          .replyMarkup(
//              KeyboardFactory.from(
//                  m.options().stream()
//                      .map(o -> new KeyboardOption(o.getText(), o.getCallback()))
//                      .toList()
//              )
//          )
//          .build();
//    }

    return null;
  }
}
