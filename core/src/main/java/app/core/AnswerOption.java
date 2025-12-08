package app.core;

public class AnswerOption {
  private final Long id;          // id варианта ответа (для callback)
  private final String text;      // текст кнопки
  private final String callback;  // callbackData (можно сформировать здесь)

  public AnswerOption(Long id, String text, String callback) {
    this.id = id;
    this.text = text;
    this.callback = callback;
  }
  public Long getId() { return id; }
  public String getText() { return text; }
  public String getCallback() { return callback; }
}
