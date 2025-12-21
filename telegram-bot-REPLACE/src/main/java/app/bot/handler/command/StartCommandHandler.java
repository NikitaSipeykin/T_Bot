package app.bot.handler.command;

import app.bot.state.UserStateService;
import app.core.broadcast.SubscriberService;
import app.module.node.texts.BotTextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {

  private final SubscriberService subscriberService;
  private final BotTextService textService;
  private final UserStateService userStateService;

  @Override
  public String command() {
    return "/start";
  }

  @Override
  public BotApiMethod<?> handle(Message message) {
    Long chatId = message.getChatId();
    String firstName = message.getFrom().getFirstName();

    subscriberService.subscribe(
        chatId,
        message.getFrom().getUserName(),
        firstName
    );

    userStateService.reset(chatId);

    return SendMessage.builder()
        .chatId(chatId.toString())
        .text(textService.format("START", firstName != null ? firstName : "друг"))
        .build();
  }
}

