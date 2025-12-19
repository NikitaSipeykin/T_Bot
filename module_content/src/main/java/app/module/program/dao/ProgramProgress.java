package app.module.program.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "program_progress")
public class ProgramProgress {

  @Id
  @Column(name = "chat_id")
  private Long chatId;

  @Column(name = "progress_level", nullable = false)
  private String progressLevel;

  @Column(name = "payment_id", nullable = false)
  private Long paymentId;

  // getters / setters

  public Long getChatId() {
    return chatId;
  }

  public void setChatId(Long chatId) {
    this.chatId = chatId;
  }

  public String getProgressLevel() {
    return progressLevel;
  }

  public void setProgressLevel(String progressLevel) {
    this.progressLevel = progressLevel;
  }

  public Long getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(Long paymentId) {
    this.paymentId = paymentId;
  }
}
