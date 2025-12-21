package app.bot.handler.command;


import app.bot.config.BotProperties;
import app.core.broadcast.BroadcastService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class BroadcastCommandHandler implements CommandHandler {

  private final BroadcastService broadcastService;
  private final BotProperties botProperties;
  private final BotTextService textService;

  @Override
  public String command() {
    return "/broadcast";
  }

  @Override
  public BotApiMethod<?> handle(Message message) {
    Long chatId = message.getChatId();
    Long userId = message.getFrom().getId();

    if (!userId.equals(botProperties.getAdminId())) {
      return SendMessage.builder()
          .chatId(chatId.toString())
          .text(textService.get(TextMarker.BROADCAST_FAIL))
          .build();
    }

    String body = message.getText().substring("/broadcast".length()).trim();
    broadcastService.broadcast(body);

    return null; // админский silent-command
  }
}
