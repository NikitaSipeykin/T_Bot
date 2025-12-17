package app.core;

public interface MessageSender {
  void sendText(Long chatId, String text);
}
