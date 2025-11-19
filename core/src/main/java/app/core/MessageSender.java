package app.core;

public interface MessageSender {
  void send(Long chatId, String text);
}
