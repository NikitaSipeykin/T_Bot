package app.bot.handler.message.course;

import app.bot.bot.responce.*;
import app.bot.handler.message.MessageHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
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
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramMessageHandler implements MessageHandler {

  private final ProgramService programService;
  private final BotTextService textService;
  private final UserStateService userStateService;

  @Override
  public UserState supports() {
    return UserState.COURSE;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();

    if (programService.checkUserAccessProgram(chatId)) {
      userStateService.setState(chatId, UserState.COURSE);
      Object response = programService.nextMessage(chatId);

      CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

      if (response instanceof CompositeProgramMessage cm) {
        log.info("PMH CompMes = " + cm);
        for (ProgramMessage m : cm.responses()) {
          compositeResponse.responses().add(new TextResponse(chatId, textService.format(m.text()), KeyboardFactory.toKeyboard(m.options())));
        }
        return compositeResponse;
      }

      if (response instanceof ProgramMessage m) {
        compositeResponse.responses().add(new TextResponse(chatId, textService.format(m.text()), KeyboardFactory.toKeyboard(m.options())));

        if (m.text().endsWith(TextMarker.AUDIO_MARKER)) {
          compositeResponse.responses().add(new MediaResponse(chatId, MediaType.AUDIO, m.text()));
        }
        return compositeResponse;
      }
    }

    return null;
  }
}
