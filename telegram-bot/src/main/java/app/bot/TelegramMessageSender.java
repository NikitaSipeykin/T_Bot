package app.bot;

import app.bot.mane.BaseTelegramBot;
import app.core.MessageSender;
import org.springframework.stereotype.Component;

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