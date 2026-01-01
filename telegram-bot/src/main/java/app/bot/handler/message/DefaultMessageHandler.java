package app.bot.handler.message;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
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
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    return new TextResponse(chatId, textService.get(TextMarker.ERROR), null);
  }
}
