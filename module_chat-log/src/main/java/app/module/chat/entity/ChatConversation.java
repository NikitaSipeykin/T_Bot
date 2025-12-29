package app.module.chat.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "chat_conversation",
    indexes = {
        @Index(name = "idx_chat_conversation_chat_id", columnList = "chat_id")
    }
)
public class ChatConversation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "chat_id", nullable = false)
  private Long chatId;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  @Column(name = "closed_at")
  private Instant closedAt;

  @OneToMany(
      mappedBy = "conversation",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  @OrderBy("createdAt ASC")
  private List<ChatMessage> messages = new ArrayList<>();

  // getters / setters


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getChatId() {
    return chatId;
  }

  public void setChatId(Long chatId) {
    this.chatId = chatId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getClosedAt() {
    return closedAt;
  }

  public void setClosedAt(Instant closedAt) {
    this.closedAt = closedAt;
  }

  public List<ChatMessage> getMessages() {
    return messages;
  }

  public void setMessages(List<ChatMessage> messages) {
    this.messages = messages;
  }
}
