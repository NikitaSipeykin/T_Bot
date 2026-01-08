package app.bot.bot;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.BotResponseProcessor;
import app.bot.config.BotProperties;
import app.bot.dispatcher.CallbackDispatcher;
import app.bot.dispatcher.CommandDispatcher;
import app.bot.dispatcher.MessageDispatcher;
import app.bot.sender.TelegramSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
  protected final BotResponseProcessor botResponseProcessor;

  protected final TelegramSender telegramSender;

  protected BaseTelegramBot(BotProperties botProperties, CallbackDispatcher callbackDispatcher,
      CommandDispatcher commandDispatcher, MessageDispatcher messageDispatcher,
      BotResponseProcessor botResponseProcessor, TelegramSender telegramSender) {
    this.botProperties = botProperties;
    this.callbackDispatcher = callbackDispatcher;
    this.commandDispatcher = commandDispatcher;
    this.messageDispatcher = messageDispatcher;
    this.botResponseProcessor = botResponseProcessor;
    this.telegramSender = telegramSender;
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
        Message message = update.getMessage();
        message.setText("successPayment");
        BotResponse response = messageDispatcher.dispatch(message);
        botResponseProcessor.process(response);
        return;
      }

      // 3. Callback
      if (update.hasCallbackQuery()) {
        CallbackQuery cb = update.getCallbackQuery();
        removeInlineButtons(cb);
        BotResponse response = callbackDispatcher.dispatch(cb);
        botResponseProcessor.process(response);
        return;
      }

      // 4. Command
      if (update.hasMessage()) {
        BotResponse response = commandDispatcher.dispatch(update.getMessage());
        if (response != null) {
          botResponseProcessor.process(response);
          return;
        }
      }

      // 5. State-based message
      if (update.hasMessage()) {
        BotResponse response = messageDispatcher.dispatch(update.getMessage());
        log.debug("response = " + response);
        if (response != null) {
          botResponseProcessor.process(response);
          return;
        }
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

  private void removeInlineButtons(CallbackQuery cb) {
    EditMessageReplyMarkup edit = new EditMessageReplyMarkup();
    edit.setChatId(cb.getMessage().getChatId());
    edit.setMessageId(cb.getMessage().getMessageId());
    edit.setReplyMarkup(null);

    try {
      execute(edit);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  // ===== Hooks for payments =====

  protected abstract void handlePreCheckout(Update update);

  protected abstract void handleSuccessfulPayment(Update update);

  protected void handleUnknown(Update update) {
    log.error("Handle unknown!!!");
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
