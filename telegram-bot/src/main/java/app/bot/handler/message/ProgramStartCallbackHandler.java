package app.bot.handler.message;

import app.bot.handler.callback.CallbackHandler;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.program.ProgramMessage;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.program.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
public class ProgramStartCallbackHandler implements CallbackHandler {

  private final ProgramService programService;
  private final BotTextService textService;
  private final UserStateService userStateService;

  @Override
  public boolean supports(String callbackData) {
    return TextMarker.PROGRAM.equals(callbackData);
  }

  @Override
  public BotApiMethod<?> handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();

    Object response = programService.startProgram(chatId);
    userStateService.setState(chatId, UserState.PROGRAM);

    if (response instanceof ProgramMessage m) {
      return SendMessage.builder()
          .chatId(chatId.toString())
          .text(textService.format(m.text()))
          .build();
    }

    return null;
  }
}
