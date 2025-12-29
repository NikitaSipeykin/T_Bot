package app.module.chat.entity;

import app.module.chat.SenderType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "chat_message",
    indexes = {
        @Index(name = "idx_chat_message_chat_id", columnList = "chat_id"),
        @Index(
            name = "idx_chat_message_conversation",
            columnList = "conversation_id, created_at"
        )
    }
)
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // --- relations ---

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false)
  private ChatConversation conversation;

  // --- fields ---

  @Column(name = "chat_id", nullable = false)
  private Long chatId;

  @Enumerated(EnumType.STRING)
  @Column(name = "sender_type", nullable = false)
  private SenderType senderType;

  @Column(name = "message_text", nullable = false, columnDefinition = "TEXT")
  private String messageText;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  // --- buttons ---

  @OneToMany(
      mappedBy = "message",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  private List<ChatMessageButton> buttons = new ArrayList<>();

  // getters / setters


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ChatConversation getConversation() {
    return conversation;
  }

  public void setConversation(ChatConversation conversation) {
    this.conversation = conversation;
  }

  public Long getChatId() {
    return chatId;
  }

  public void setChatId(Long chatId) {
    this.chatId = chatId;
  }

  public SenderType getSenderType() {
    return senderType;
  }

  public void setSenderType(SenderType senderType) {
    this.senderType = senderType;
  }

  public String getMessageText() {
    return messageText;
  }

  public void setMessageText(String messageText) {
    this.messageText = messageText;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public List<ChatMessageButton> getButtons() {
    return buttons;
  }

  public void setButtons(List<ChatMessageButton> buttons) {
    this.buttons = buttons;
  }
}
