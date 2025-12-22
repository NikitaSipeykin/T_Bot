package app.bot.dispatcher;

import app.bot.handler.state_message.StateMessageHandler;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MessageDispatcher {

  private final UserStateService stateService;
  private final Map<UserState, StateMessageHandler> handlers;

  public MessageDispatcher(
      UserStateService stateService,
      List<StateMessageHandler> handlers
  ) {
    this.stateService = stateService;
    this.handlers = handlers.stream()
        .collect(Collectors.toMap(StateMessageHandler::state, h -> h));
  }

  public void dispatch(Update update) {
    Long chatId = update.getMessage().getChatId();
    UserState state = stateService.getState(chatId);

    StateMessageHandler handler = handlers.get(state);

    if (handler == null) {
      throw new IllegalStateException("No handler for state " + state);
    }

    handler.handle(update);
  }
}
