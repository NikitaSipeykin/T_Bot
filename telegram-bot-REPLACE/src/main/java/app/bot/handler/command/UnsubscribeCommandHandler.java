package app.bot.handler.command;

import app.bot.bot.responce.BotResponse;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.core.broadcast.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class UnsubscribeCommandHandler implements CommandHandler {

  private final SubscriberService subscriberService;
  private final BotTextService textService;

  @Override
  public String command() {
    return "/unsubscribe";
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();

    subscriberService.unsubscribe(chatId);

    return null;
  }
}

