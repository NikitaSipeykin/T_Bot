package app.bot.dispatcher;

import app.bot.bot.responce.BotResponse;
import app.bot.handler.message.MessageHandler;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MessageDispatcher {

  private final Map<UserState, MessageHandler> handlers;
  private final UserStateService userStateService;

  public MessageDispatcher(
      List<MessageHandler> handlers,
      UserStateService userStateService
  ) {
    this.handlers = handlers.stream().collect(Collectors.toMap(MessageHandler::supports, h -> h));
    this.userStateService = userStateService;
  }

  public BotResponse dispatch(Message message) {
    Long chatId = message.getChatId();
    UserState state = userStateService.getState(chatId);
    String text = message.getText();

    log.info("Message text = " + text);

    if (text.equals("successPayment")){
      MessageHandler successHandler = handlers.get(UserState.SUCCESS_PAYMENT);
      return successHandler.handle(message);
    }

    log.info("state = " + state);
    MessageHandler handler = handlers.get(state);
    if (handler == null) {
      log.warn("No MessageHandler for state={}", state);
      return null;
    }

    return handler.handle(message);
  }
}
