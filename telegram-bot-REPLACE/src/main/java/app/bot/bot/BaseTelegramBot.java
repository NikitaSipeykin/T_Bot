package app.bot.bot;

import app.bot.config.BotProperties;
import app.bot.dispatcher.CallbackDispatcher;
import app.bot.dispatcher.CommandDispatcher;
import app.bot.dispatcher.MessageDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public abstract class BaseTelegramBot extends TelegramLongPollingBot {

  private final BotProperties botProperties;
  private final CallbackDispatcher callbackDispatcher;
  private final CommandDispatcher commandDispatcher;
  private final MessageDispatcher messageDispatcher;

  protected BaseTelegramBot(
      BotProperties botProperties,
      CallbackDispatcher callbackDispatcher,
      CommandDispatcher commandDispatcher,
      MessageDispatcher messageDispatcher
  ) {
    this.botProperties = botProperties;
    this.callbackDispatcher = callbackDispatcher;
    this.commandDispatcher = commandDispatcher;
    this.messageDispatcher = messageDispatcher;
  }

  @Override
  public final void onUpdateReceived(Update update) {
    if (update == null) {
      return;
    }

    try {
      // 1. PreCheckout
      if (update.hasPreCheckoutQuery()) {
        handlePreCheckout(update);
        return;
      }

      // 2. Successful payment
      if (update.hasMessage() && update.getMessage().hasSuccessfulPayment()) {
        handleSuccessfulPayment(update);
        return;
      }

      // 3. Callback
      if (update.hasCallbackQuery()) {
        BotApiMethod<?> method = callbackDispatcher.dispatch(update.getCallbackQuery());
        executeSafely(method);
        return;
      }

      // 4. Command
      if (update.hasMessage()) {
        BotApiMethod<?> method = commandDispatcher.dispatch(update.getMessage());
        if (method != null) {
          executeSafely(method);
          return;
        }
      }

      // 5. State-based message
      if (update.hasMessage()) {
        messageDispatcher.dispatch(update);
        return;
      }

      handleUnknown(update);

    } catch (Exception e) {
      log.error("Unhandled exception while processing update", e);
    }
  }

  protected void executeSafely(BotApiMethod<?> method) {
    if (method == null) {
      return;
    }
    try {
      execute(method);
    } catch (TelegramApiException e) {
      log.error("Telegram API execution failed", e);
    }
  }

  // ===== Hooks for payments =====

  protected abstract void handlePreCheckout(Update update);

  protected abstract void handleSuccessfulPayment(Update update);

  protected void handleUnknown(Update update) {
    // no-op by default
  }

  // ===== Telegram credentials =====

  @Override
  public final String getBotUsername() {
    return botProperties.getUsername();
  }

  @Override
  public final String getBotToken() {
    return botProperties.getToken();
  }
}
