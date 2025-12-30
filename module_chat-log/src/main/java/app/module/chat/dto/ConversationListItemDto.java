package app.module.chat.dto;

import java.time.Instant;

public class ConversationListItemDto {

  private Long conversationId;
  private Long chatId;

  private String username;
  private String lastMessageText;
  private Instant lastMessageAt;

  public Long getConversationId() {
    return conversationId;
  }

  public void setConversationId(Long conversationId) {
    this.conversationId = conversationId;
  }

  public Long getChatId() {
    return chatId;
  }

  public void setChatId(Long chatId) {
    this.chatId = chatId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getLastMessageText() {
    return lastMessageText;
  }

  public void setLastMessageText(String lastMessageText) {
    this.lastMessageText = lastMessageText;
  }

  public Instant getLastMessageAt() {
    return lastMessageAt;
  }

  public void setLastMessageAt(Instant lastMessageAt) {
    this.lastMessageAt = lastMessageAt;
  }
}


