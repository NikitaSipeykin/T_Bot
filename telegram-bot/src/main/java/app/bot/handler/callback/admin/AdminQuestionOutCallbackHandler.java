package app.bot.handler.callback.admin;

import app.bot.bot.responce.*;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.callback.CallbackHandler;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.module.node.texts.BotTextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
public class AdminQuestionOutCallbackHandler implements CallbackHandler {

  private final UserStateService userStateService;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals("QUESTION_OUT");
  }

  @Override
  public BotResponse handle(CallbackQuery callbackQuery) {
    Long chatId = callbackQuery.getMessage().getChatId();
    userStateService.setState(chatId, UserState.DEFAULT);

    return new TextResponse(chatId, "Вы вышли из режима отправки сообщений пользователям!", null);
  }
}
