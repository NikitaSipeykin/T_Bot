package app.module.program.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "program_progress")
public class ProgramProgress {

  @Id
  @Column(name = "chat_id")
  private Long chatId;

  @Column(name = "payment_date", nullable = false)
  private LocalDate paymentDate;

  @Column(name = "progress_level", nullable = false)
  private String progressLevel;

  // Getters / Setters

  public Long getChatId() {
    return chatId;
  }

  public void setChatId(Long chatId) {
    this.chatId = chatId;
  }

  public LocalDate getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(LocalDate paymentDate) {
    this.paymentDate = paymentDate;
  }

  public String getProgressLevel() {
    return progressLevel;
  }

  public void setProgressLevel(String progressLevel) {
    this.progressLevel = progressLevel;
  }
}

