package app.bot.config;

import app.bot.bot.BaseTelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Slf4j
public class BotRegistrar {

  private final BotProperties botProperties;
  private final BaseTelegramBot bot;

  public BotRegistrar(BotProperties botProperties, BaseTelegramBot bot) {
    this.botProperties = botProperties;
    this.bot = bot;
  }

  @PostConstruct
  public void register() {
    try {
      TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

      if (isWebhookMode()) {
        log.debug("Starting Telegram bot in WEBHOOK mode");
        // Webhook регистрируется TelegramMessageSender / controller'ом
      } else {
        log.debug("Starting Telegram bot in LONG POLLING mode");
        botsApi.registerBot(bot);
      }

      log.debug(
          "Telegram bot registered successfully. Username={}",
          botProperties.getUsername()
      );

    } catch (TelegramApiException e) {
      log.error("Failed to register Telegram bot", e);
      throw new IllegalStateException("Telegram bot startup failed", e);
    }
  }

  private boolean isWebhookMode() {
    return botProperties.getWebhookUrl() != null
           && !botProperties.getWebhookUrl().isBlank();
  }
}
