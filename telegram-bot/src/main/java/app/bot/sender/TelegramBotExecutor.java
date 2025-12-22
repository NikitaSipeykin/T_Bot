package app.bot.sender;

import app.bot.bot.BaseTelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBotExecutor implements TelegramExecutor {

  private final ObjectProvider<BaseTelegramBot> botProvider;

  @Override
  public void execute(BotApiMethod<?> method) {
    if (method == null) {
      return;
    }

    BaseTelegramBot bot = botProvider.getIfAvailable();
    if (bot == null) {
      log.error("Telegram bot not available");
      return;
    }

    try {
      bot.execute(method);
    } catch (TelegramApiException e) {
      log.error("Telegram execute error", e);
    }
  }
}

