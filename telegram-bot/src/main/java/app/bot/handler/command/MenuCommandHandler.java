package app.bot.handler.command;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.program.ProgramProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuCommandHandler implements CommandHandler {

  private final BotTextService textService;
  private final ProgramProgressService progressService;

  @Override
  public String command() {
    return "/menu";
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    String firstName = message.getFrom().getFirstName();
    TextResponse text;


    if (progressService.isUserInProgram(chatId)) {
      if (progressService.getTodayLimit(chatId).equals("PROGRAM_SVADHISHTHANA_AUDIO") ||
          progressService.getTodayLimit(chatId).equals("PROGRAM_SVADHISHTHANA_PRACTICE_TASK") ||
          progressService.getTodayLimit(chatId).equals("PROGRAM_SVADHISHTHANA_FINISH")){
        text = new TextResponse(chatId, textService.format(TextMarker.MENU_LIMIT_SVADHISHTHANA),
            KeyboardFactory.from(List.of(new KeyboardOption("Муладхара", TextMarker.MENU_LIMIT_SVADHISHTHANA))));
      }
      else if (progressService.getTodayLimit(chatId).equals("PROGRAM_MANIPURA_AUDIO") ||
               progressService.getTodayLimit(chatId).equals("PROGRAM_MANIPURA_PRACTICE_TASK") ||
               progressService.getTodayLimit(chatId).equals("PROGRAM_MANIPURA_FINISH")){
        text = new TextResponse(chatId, textService.format(TextMarker.MENU_LIMIT_MANIPURA),
            KeyboardFactory.from(List.of(
                new KeyboardOption("Муладхара", TextMarker.MENU_LIMIT_SVADHISHTHANA),
                new KeyboardOption("Свадхистана", TextMarker.MENU_LIMIT_MANIPURA))));
      }
      else if (progressService.getTodayLimit(chatId).equals("PROGRAM_ANAHATA_AUDIO") ||
               progressService.getTodayLimit(chatId).equals("PROGRAM_ANAHATA_PRACTICE_TASK") ||
               progressService.getTodayLimit(chatId).equals("PROGRAM_ANAHATA_FINISH")){
        text = new TextResponse(chatId, textService.format(TextMarker.MENU_LIMIT_ANAHATA),
            KeyboardFactory.from(List.of(
                new KeyboardOption("Муладхара", TextMarker.MENU_LIMIT_SVADHISHTHANA),
                new KeyboardOption("Свадхистана", TextMarker.MENU_LIMIT_MANIPURA),
                new KeyboardOption("Манипура", TextMarker.MENU_LIMIT_ANAHATA))));
      }
      else if (progressService.getTodayLimit(chatId).equals("PROGRAM_VISHUDDHA_AUDIO") ||
               progressService.getTodayLimit(chatId).equals("PROGRAM_VISHUDDHA_PRACTICE_TASK") ||
               progressService.getTodayLimit(chatId).equals("PROGRAM_VISHUDDHA_FINISH")){
        text = new TextResponse(chatId, textService.format(TextMarker.MENU_LIMIT_VISHUDDHA),
            KeyboardFactory.from(List.of(
                new KeyboardOption("Муладхара", TextMarker.MENU_LIMIT_SVADHISHTHANA),
                new KeyboardOption("Свадхистана", TextMarker.MENU_LIMIT_MANIPURA),
                new KeyboardOption("Манипура", TextMarker.MENU_LIMIT_ANAHATA),
                new KeyboardOption("Анахата", TextMarker.MENU_LIMIT_VISHUDDHA))));
      }
      else if (progressService.getTodayLimit(chatId).equals("PROGRAM_AJNA_AUDIO") ||
               progressService.getTodayLimit(chatId).equals("PROGRAM_AJNA_PRACTICE_TASK") ||
               progressService.getTodayLimit(chatId).equals("PROGRAM_AJNA_FINISH")){
        text = new TextResponse(chatId, textService.format(TextMarker.MENU_LIMIT_AJNA),
            KeyboardFactory.from(List.of(
                new KeyboardOption("Муладхара", TextMarker.MENU_LIMIT_SVADHISHTHANA),
                new KeyboardOption("Свадхистана", TextMarker.MENU_LIMIT_MANIPURA),
                new KeyboardOption("Манипура", TextMarker.MENU_LIMIT_ANAHATA),
                new KeyboardOption("Анахата", TextMarker.MENU_LIMIT_VISHUDDHA),
                new KeyboardOption("Вишудха", TextMarker.MENU_LIMIT_AJNA))));
      }
      else if (progressService.getTodayLimit(chatId).equals("PROGRAM_SAHASRARA_AUDIO") ||
               progressService.getTodayLimit(chatId).equals("PROGRAM_SAHASRARA_PRACTICE_TASK") ||
               progressService.getTodayLimit(chatId).equals("PROGRAM_SAHASRARA_END")){
        text = new TextResponse(chatId, textService.format(TextMarker.MENU_LIMIT_FULL),
            KeyboardFactory.from(List.of(
                new KeyboardOption("Муладхара", TextMarker.MENU_LIMIT_SVADHISHTHANA),
                new KeyboardOption("Свадхистана", TextMarker.MENU_LIMIT_MANIPURA),
                new KeyboardOption("Манипура", TextMarker.MENU_LIMIT_ANAHATA),
                new KeyboardOption("Анахата", TextMarker.MENU_LIMIT_VISHUDDHA),
                new KeyboardOption("Вишудха", TextMarker.MENU_LIMIT_AJNA),
                new KeyboardOption("Аджна", TextMarker.MENU_LIMIT_FULL))));
      }
      else {
        text = new TextResponse(chatId, textService.format(TextMarker.MENU_IN_PROGRAM), null);
      }
     return text;
    }

    text = new TextResponse(chatId, textService.format(TextMarker.MENU_DEFAULT), null);

    return text;
  }
}
