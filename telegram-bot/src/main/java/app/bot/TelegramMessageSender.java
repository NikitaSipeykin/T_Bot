package app.bot;

import app.core.AnswerOption;
import app.core.MessageSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class TelegramMessageSender implements MessageSender {
  private final BaseTelegramBot bot;

  public TelegramMessageSender(BaseTelegramBot bot) {
    this.bot = bot;
  }

  @Override
  public void sendText(Long chatId, String text) {
    bot.sendMessage(chatId, text, null);
  }

}