package app.bot.handler.callback.course;

import app.bot.bot.responce.BotResponse;
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
import app.bot.facade.AnalyticsFacade;

import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class MenuLimitCallbackHandler implements CallbackHandler {

  private final ProgramService programService;
  private final BotTextService textService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analyticsFacade;

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
      analyticsFacade.trackButtonClick(query, message);

      String targetBlock = null;

      switch (message) {
        case TextMarker.MENU_LIMIT_SVADHISHTHANA -> targetBlock = TextMarker.PROGRAM_MULADHARA_INTRO;
        case TextMarker.MENU_LIMIT_MANIPURA -> targetBlock = TextMarker.PROGRAM_SVADHISHTHANA_INTRO;
        case TextMarker.MENU_LIMIT_ANAHATA -> targetBlock = TextMarker.PROGRAM_MANIPURA_INTRO;
        case TextMarker.MENU_LIMIT_VISHUDDHA -> targetBlock = TextMarker.PROGRAM_ANAHATA_INTRO;
        case TextMarker.MENU_LIMIT_AJNA -> targetBlock = TextMarker.PROGRAM_VISHUDDHA_INTRO;
        case TextMarker.MENU_LIMIT_FULL -> targetBlock = TextMarker.PROGRAM_AJNA_INTRO;
      }

      if (targetBlock != null) {
        programService.moveToTopic(chatId, targetBlock);

        // 👉 аналитика просмотра блока
        analyticsFacade.trackBlockView(chatId, targetBlock,
            Map.of(
                "source", "menu_limit",
                "menu", message
            )
        );
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
