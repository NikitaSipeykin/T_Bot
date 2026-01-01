package app.bot.keyboard;

import app.core.test.AnswerOption;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public final class KeyboardFactory {

  private KeyboardFactory() {
  }

  public static InlineKeyboardMarkup from(List<KeyboardOption> options) {
    return build(options, KeyboardOption::text, KeyboardOption::callback);
  }

  public static InlineKeyboardMarkup toKeyboard(List<AnswerOption> options) {
    return build(options, AnswerOption::getText, AnswerOption::getCallback);
  }

  private static <T> InlineKeyboardMarkup build(List<T> options, java.util.function.Function<T, String> textMapper,
      java.util.function.Function<T, String> callbackMapper
  ) {

    List<List<InlineKeyboardButton>> rows = new ArrayList<>();

    for (T option : options) {
      InlineKeyboardButton button = new InlineKeyboardButton();
      button.setText(textMapper.apply(option));
      button.setCallbackData(callbackMapper.apply(option));

      rows.add(List.of(button));
    }

    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    markup.setKeyboard(rows);
    return markup;
  }
}
