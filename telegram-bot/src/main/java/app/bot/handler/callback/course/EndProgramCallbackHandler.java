package app.bot.handler.callback.course;

import app.bot.bot.responce.*;
import app.bot.handler.callback.CallbackHandler;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.program.ProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Slf4j
@Component
@RequiredArgsConstructor
public class EndProgramCallbackHandler implements CallbackHandler {

  private final ProgramService programService;
  private final BotTextService textService;
  private final UserStateService userStateService;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.END_PROGRAM);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.DEFAULT);
    return new TextResponse(chatId, textService.get(TextMarker.PROGRAM_THANKS), null);
  }
}