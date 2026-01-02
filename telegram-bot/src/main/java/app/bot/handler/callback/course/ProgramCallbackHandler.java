package app.bot.handler.callback.course;


import app.bot.bot.responce.*;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.program.CompositeProgramMessage;
import app.core.program.ProgramMessage;
import app.module.chat.service.ChatHistoryService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.program.ProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.ArrayList;


@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramCallbackHandler implements CallbackHandler {

  private final ProgramService programService;
  private final BotTextService textService;
  private final UserStateService userStateService;
  private final ChatHistoryService chatHistoryService;
  private final AnalyticsFacade analyticsFacade;



  @Override
  public boolean supports(String callbackData) {
    return TextMarker.PROGRAM.equals(callbackData);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    analyticsFacade.trackBlockView(chatId, TextMarker.PROGRAM);

    if (programService.checkUserAccessProgram(chatId)) {
      userStateService.setState(chatId, UserState.COURSE);
      chatHistoryService.logUserMessage(chatId, "Пользователь нажал на кнопку");


      if (programService.isLimitReached(chatId)) {
        chatHistoryService.logBotMessage(chatId, textService.format(TextMarker.TODAY_LIMIT));
        analyticsFacade.trackBlockView(chatId, TextMarker.TODAY_LIMIT);
        return new TextResponse(chatId, textService.format(TextMarker.TODAY_LIMIT), null);
      }

      Object response = programService.nextMessage(chatId);
      CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

      log.debug("response = " + response);


      if (response instanceof CompositeProgramMessage cm) {
        for (ProgramMessage m : cm.responses()) {
          analyticsFacade.trackBlockView(chatId, m.text());
          chatHistoryService.logBotMessage(chatId, textService.format(m.text()));
          compositeResponse.responses().add(new TextResponse(chatId, textService.format(m.text()),
              KeyboardFactory.toKeyboard(m.options())));
        }
        return compositeResponse;
      }

      if (response instanceof ProgramMessage m) {
        analyticsFacade.trackBlockView(chatId, m.text());
        chatHistoryService.logBotMessage(chatId, textService.format(m.text()));
        compositeResponse.responses().add(new TextResponse(chatId, textService.format(m.text()),
            KeyboardFactory.toKeyboard(m.options())));


        if (m.text().endsWith(TextMarker.AUDIO_MARKER)) {
          compositeResponse.responses().add(new MediaResponse(chatId, MediaType.AUDIO, m.text()));
        }

        log.debug("compositeResponse = " + compositeResponse);
        return compositeResponse;
      }
    } else {
      Object response = programService.startProgram(chatId);
      userStateService.setState(chatId, UserState.COURSE);

      log.debug("start course");
      log.debug("response = " + response);
      if (response instanceof ProgramMessage m) {
        analyticsFacade.trackBlockView(chatId, m.text());
        chatHistoryService.logBotMessage(chatId, textService.format(m.text()));
        return new TextResponse(chatId, textService.format(m.text()),
            KeyboardFactory.toKeyboard(m.options()));
      }
    }

    log.warn("USER IS MOT IM THE PROGRAM");
    return null;
  }
}