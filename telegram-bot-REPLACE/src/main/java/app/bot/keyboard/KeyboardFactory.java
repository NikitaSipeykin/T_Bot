package app.bot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public final class KeyboardFactory {

  private KeyboardFactory() {}

  public static InlineKeyboardMarkup from(List<KeyboardOption> options) {
    List<List<InlineKeyboardButton>> rows = new ArrayList<>();

    for (KeyboardOption option : options) {
      InlineKeyboardButton button = new InlineKeyboardButton();
      button.setText(option.text());
      button.setCallbackData(option.callback());

      rows.add(List.of(button));
    }

    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    markup.setKeyboard(rows);
    return markup;
  }
}
