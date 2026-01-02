package app.bot.handler.message.course;

import app.bot.bot.responce.*;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.message.MessageHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
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
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramMessageHandler implements MessageHandler {

  private final ProgramService programService;
  private final BotTextService textService;
  private final UserStateService userStateService;
  private final ChatHistoryService chatHistoryService;
  private final AnalyticsFacade analyticsFacade;


  @Override
  public UserState supports() {
    return UserState.COURSE;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();

    if (programService.checkUserAccessProgram(chatId)) {
      userStateService.setState(chatId, UserState.COURSE);
      chatHistoryService.logUserMessage(chatId, message.getText());


      if (programService.isLimitReached(chatId)){
        chatHistoryService.logBotMessage(chatId, textService.format(TextMarker.TODAY_LIMIT));
        analyticsFacade.trackBlockView(
            chatId,
            TextMarker.TODAY_LIMIT,
            Map.of(
                "source", "program_message",
                "reason", "daily_limit"
            )
        );
        return new TextResponse(chatId, textService.format(TextMarker.TODAY_LIMIT), null);
      }

      Object response = programService.nextMessage(chatId);
      CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

      if (response instanceof CompositeProgramMessage cm) {
        log.info("PMH CompMes = " + cm);
        for (ProgramMessage m : cm.responses()) {
          chatHistoryService.logBotMessage(chatId, textService.format(m.text()));
          analyticsFacade.trackBlockView(chatId, m.text(),
              Map.of(
                  "source", "program_message",
                  "type", "composite"
              )
          );

          compositeResponse.responses().add(new TextResponse(chatId, textService.format(m.text()),
              KeyboardFactory.toKeyboard(m.options())));
        }
        return compositeResponse;
      }

      if (response instanceof ProgramMessage m) {
        chatHistoryService.logBotMessage(chatId, textService.format(m.text()));
        analyticsFacade.trackBlockView(chatId, m.text(),
            Map.of(
                "source", "program_message",
                "type", "single"
            )
        );

        compositeResponse.responses().add(new TextResponse(chatId, textService.format(m.text()),
            KeyboardFactory.toKeyboard(m.options())));

        if (m.text().endsWith(TextMarker.AUDIO_MARKER)) {
          compositeResponse.responses().add(new MediaResponse(chatId, MediaType.AUDIO, m.text()));
        }
        return compositeResponse;
      }
    }

    return null;
  }
}
