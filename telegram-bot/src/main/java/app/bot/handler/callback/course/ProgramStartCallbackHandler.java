package app.bot.handler.callback.course;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.program.ProgramMessage;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.program.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProgramStartCallbackHandler implements CallbackHandler {

  private final ProgramService programService;
  private final BotTextService textService;
  private final UserStateService userStateService;

  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.PROGRAM);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();

    Object response = programService.startProgram(chatId);
    userStateService.setState(chatId, UserState.COURSE);

    if (response instanceof ProgramMessage m) {
      return new TextResponse(chatId, textService.format(m.text()),
          KeyboardFactory.toKeyboard(m.options()));
    }

    return null;
  }
}
