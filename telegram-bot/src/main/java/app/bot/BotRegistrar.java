package app.bot;

import app.bot.mane.BaseTelegramBot;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotRegistrar {

  private final BaseTelegramBot bot;

  public BotRegistrar(BaseTelegramBot bot) {
    this.bot = bot;
  }

  @PostConstruct
  public void start() throws Exception {
    TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
    api.registerBot(bot);
  }
}
