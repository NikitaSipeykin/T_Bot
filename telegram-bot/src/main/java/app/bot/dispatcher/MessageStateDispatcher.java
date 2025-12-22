package app.bot.dispatcher;

import app.bot.handler.message.MessageHandler;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MessageStateDispatcher {

    private final Map<UserState, MessageHandler> handlers;
    private final UserStateService userStateService;

    public MessageStateDispatcher(
        List<MessageHandler> handlers,
        UserStateService userStateService
    ) {
        this.handlers = handlers.stream()
            .collect(Collectors.toMap(MessageHandler::supports, h -> h));
        this.userStateService = userStateService;
    }

    public BotApiMethod<?> dispatch(Message message) {
        Long chatId = message.getChatId();
        UserState state = userStateService.getState(chatId);

        MessageHandler handler = handlers.get(state);
        if (handler == null) {
            log.warn("No MessageHandler for state={}", state);
            return null;
        }

        return handler.handle(message);
    }
}
