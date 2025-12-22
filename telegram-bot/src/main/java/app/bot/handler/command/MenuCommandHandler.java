package app.bot.handler.command;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class MenuCommandHandler implements CommandHandler{

  private final BotTextService textService;

  @Override
  public String command() {
    return "/menu";
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    String firstName = message.getFrom().getFirstName();

    TextResponse text = new TextResponse(chatId, textService.format(TextMarker.MENU_DEFAULT), null);
    return text;
  }
}
