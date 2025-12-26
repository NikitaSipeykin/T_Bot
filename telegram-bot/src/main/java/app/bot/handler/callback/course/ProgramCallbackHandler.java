package app.bot.handler.callback.course;

import app.bot.bot.Commands;
import app.bot.bot.responce.*;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.program.CompositeProgramMessage;
import app.core.program.ProgramMessage;
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


  @Override
  public boolean supports(String callbackData) {
    return TextMarker.PROGRAM.equals(callbackData);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    log.info("PCH handle() = ");

    if (programService.checkUserAccessProgram(chatId)) {
      userStateService.setState(chatId, UserState.COURSE);
      Object response = programService.nextMessage(chatId);

      log.info("response = " + response);

      CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

      if (response instanceof CompositeProgramMessage cm){
        for (ProgramMessage m : cm.responses()) {
          compositeResponse.responses().add(new TextResponse(chatId, textService.format(m.text()),
              KeyboardFactory.toKeyboard(m.options())));
        }
        return compositeResponse;
      }

      if (response instanceof ProgramMessage m) {
          compositeResponse.responses().add(new TextResponse(chatId, textService.format(m.text()),
              KeyboardFactory.toKeyboard(m.options())));


        if (m.text().endsWith(TextMarker.AUDIO_MARKER)){
          compositeResponse.responses().add(new MediaResponse(chatId, MediaType.AUDIO, m.text()));
        }

        log.info("compositeResponse = " + compositeResponse);
        return compositeResponse;
      }
    }
    else {
      Object response = programService.startProgram(chatId);
      userStateService.setState(chatId, UserState.COURSE);

      log.info("start course");
      log.info("response = " + response);
      if (response instanceof ProgramMessage m) {
        return new TextResponse(chatId, textService.format(m.text()),
            KeyboardFactory.toKeyboard(m.options()));
      }
    }

    log.error("USER IS MOT IM THE PROGRAM");
    return null;
  }
}