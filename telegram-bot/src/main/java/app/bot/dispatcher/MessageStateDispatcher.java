package app.bot.dispatcher;

import app.bot.handler.message.MessageHandler;
import app.bot.handler.state_message.StateMessageHandler;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MessageStateDispatcher {
    private final UserStateService stateService;
    private final Map<UserState, StateMessageHandler> handlers;

    public MessageStateDispatcher(
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
