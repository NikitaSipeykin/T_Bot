package app.bot.handler.callback.course;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.CompositeResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
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
public class MenuLimitCallbackHandler implements CallbackHandler {

  private final ProgramService programService;
  private final BotTextService textService;
  private final UserStateService userStateService;

  @Override
  public boolean supports(String callbackData) {
    return TextMarker.MENU_LIMIT_SVADHISHTHANA.equals(callbackData) ||
           TextMarker.MENU_LIMIT_MANIPURA.equals(callbackData) ||
           TextMarker.MENU_LIMIT_ANAHATA.equals(callbackData) ||
           TextMarker.MENU_LIMIT_VISHUDDHA.equals(callbackData) ||
           TextMarker.MENU_LIMIT_AJNA.equals(callbackData) ||
           TextMarker.MENU_LIMIT_FULL.equals(callbackData);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();

    if (programService.checkUserAccessProgram(chatId)) {
      userStateService.setState(chatId, UserState.COURSE);
      String message = query.getData();
      log.info("Callback data message = " + message);

      switch (message){
        case TextMarker.MENU_LIMIT_SVADHISHTHANA -> programService.moveToTopic(chatId, TextMarker.PROGRAM_MULADHARA_INTRO);
        case TextMarker.MENU_LIMIT_MANIPURA -> programService.moveToTopic(chatId, TextMarker.PROGRAM_SVADHISHTHANA_INTRO);
        case TextMarker.MENU_LIMIT_ANAHATA -> programService.moveToTopic(chatId, TextMarker.PROGRAM_MANIPURA_INTRO);
        case TextMarker.MENU_LIMIT_VISHUDDHA -> programService.moveToTopic(chatId, TextMarker.PROGRAM_ANAHATA_INTRO);
        case TextMarker.MENU_LIMIT_AJNA -> programService.moveToTopic(chatId, TextMarker.PROGRAM_VISHUDDHA_INTRO);
        case TextMarker.MENU_LIMIT_FULL -> programService.moveToTopic(chatId, TextMarker.PROGRAM_AJNA_INTRO);
      }

      Object response = programService.nextMessage(chatId);

      if (response instanceof ProgramMessage m) {
        return new TextResponse(chatId, textService.format(m.text()),
            KeyboardFactory.toKeyboard(m.options()));
      }
      log.error("Something got wrong!!!");
    }
    return null;
  }
}
