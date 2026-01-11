package app.bot.handler.callback.course;

import app.bot.bot.responce.*;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryProgramCallbackHandler implements CallbackHandler {

  private final BotTextService textService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analyticsFacade;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.HISTORY);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    return new HistoryResponse(chatId);
  }
}