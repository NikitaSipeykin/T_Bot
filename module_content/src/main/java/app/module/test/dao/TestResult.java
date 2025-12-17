package app.module.test.dao;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_result")
public class TestResult {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "topic_id")
  private TestTopic topic;

  private Long chatId;
  private Double score;
  private LocalDateTime finishedAt = LocalDateTime.now();
  private Integer lastQuestionNum = 0;

  public void addScore(double value) {
    if (this.score == null) {
      this.score = 0.0;
    }
    this.score = this.score + value;
  }

  public boolean canAnswer(int questionNum) {
    return this.lastQuestionNum == null || questionNum > this.lastQuestionNum;
  }

  public void markAnswered(int questionNum) {
    this.lastQuestionNum = questionNum;
  }

  public TestResult() {
  }

  public TestResult(Long chatId, TestTopic topic, Double score) {
    this.chatId = chatId;
    this.topic = topic;
    this.score = score;
  }

  public void setChatId(Long chatId) {
    this.chatId = chatId;
  }

  public void setTopic(TestTopic topic) {
    this.topic = topic;
  }

  public Long getId() {
    return this.id;
  }

  public Long getChatId() {
    return this.chatId;
  }

  public TestTopic getTopic() {
    return this.topic;
  }

  public Double getScore() {
    return this.score;
  }

  public LocalDateTime getFinishedAt() {
    return this.finishedAt;
  }
}
