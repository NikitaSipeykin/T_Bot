package app.bot.demo;

public enum TEXT {
  START("""
      Привет, %s! 👋
      Я — бот, который всегда рядом, чтобы помочь вам с задачами, ответить на вопросы и сделать взаимодействие удобнее.
      """),

  ERROR("Что-то пошло не так 😅"),

  FIRST_STEP("Текст для шага 1"),

  SECOND_STEP("Введите дату рождения"),

  UNSUBSCRIBE("Вы отписаны."),

  BROADCAST_FAIL("Нет доступа.");

  // ================================================

  private final String text;

  TEXT(String text) {
    this.text = text;
  }

  public String get() {
    return text;
  }

  public String format(Object... args) {
    return String.format(text, args);
  }
}


