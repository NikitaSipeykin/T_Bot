package app.bot.handler.message;

import app.bot.state.UserState;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;


@Component
@RequiredArgsConstructor
public class DefaultMessageHandler implements MessageHandler {

  private final BotTextService textService;

  @Override
  public UserState supports() {
    return UserState.DEFAULT;
  }

  @Override
  public BotApiMethod<?> handle(Message message) {
    return SendMessage.builder()
        .chatId(message.getChatId().toString())
        .text(textService.get(TextMarker.ERROR))
        .build();
  }
}
