package app.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public abstract class BaseTelegramBot extends TelegramLongPollingBot {
  private static final Logger log = LoggerFactory.getLogger(BaseTelegramBot.class);

  @Autowired
  protected BotProperties props;

  @Override
  public void onUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().hasText()) {
      messageProcessing(update);
    }
    if (update.hasCallbackQuery()) {
      callbackProcessing(update);
    }
  }

  // ======== PROPERTIES =======
  @Override
  public String getBotUsername() {
    return props.getUsername();
  }

  @Override
  public String getBotToken() {
    return props.getToken();
  }

  // ======== TOOLS ============
  public void sendMessage(Long chatId, String text, List<List<InlineKeyboardButton>> buttons) {
    SendMessage message = new SendMessage(chatId.toString(), text);

    if (buttons != null && !buttons.isEmpty()) {
      InlineKeyboardMarkup markup = new InlineKeyboardMarkup(buttons);
      message.setReplyMarkup(markup);
    }

    try {
      execute(message);
    } catch (TelegramApiException e) {
      log.error("Error sending message to chatId={}", chatId, e);
    }
  }

  public InlineKeyboardButton button(String text, String data) {
    return InlineKeyboardButton.builder()
        .text(text)
        .callbackData(data)
        .build();
  }

  public List<List<InlineKeyboardButton>> keyboard(InlineKeyboardButton... buttons) {
    List<List<InlineKeyboardButton>> rows = new ArrayList<>();
    for (InlineKeyboardButton b : buttons) {
      rows.add(Collections.singletonList(b));
    }
    return rows;
  }

  // ======== ABSTRACT ========
  public abstract void messageProcessing(Update update);
  public abstract void callbackProcessing(Update update);
}
