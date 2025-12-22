package app.bot.handler.state_message;

import app.bot.sender.TelegramMessageSender;
import app.bot.state.UserState;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class DefaultStateHandler implements StateMessageHandler {

  private final TelegramMessageSender sender;
  private final BotTextService text;

  @Override
  public UserState state() {
    return UserState.DEFAULT;
  }

  @Override
  public void handle(Update update) {
    Long chatId = update.getMessage().getChatId();

    sender.sendMessage(
        new SendMessage(
            chatId.toString(),
            text.get(TextMarker.ERROR)
        )
    );
  }
}
