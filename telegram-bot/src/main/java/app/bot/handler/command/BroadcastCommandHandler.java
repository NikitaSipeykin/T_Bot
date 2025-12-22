package app.bot.handler.command;


import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.config.BotProperties;
import app.core.broadcast.BroadcastService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
@Slf4j
public class BroadcastCommandHandler implements CommandHandler {

  private final BroadcastService broadcastService;
  private final BotProperties botProperties;
  private final BotTextService textService;

  @Override
  public String command() {
    return "/broadcast";
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    Long userId = message.getFrom().getId();

    log.debug("userId = " + userId + "\n" +
             "botProperties.getAdminId() = " + botProperties.getAdminId() + "\n" +
             "is equals = " + userId.equals(botProperties.getAdminId()));
    if (!userId.equals(botProperties.getAdminId())) {
      return new TextResponse(chatId, textService.format(TextMarker.BROADCAST_FAIL),null);
    }

    String body = message.getText().substring("/broadcast ".length()).trim();
    broadcastService.broadcast(body);

    return new TextResponse(chatId, textService.format(TextMarker.BROADCAST_SUCCESS),null);
  }
}
