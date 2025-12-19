package app.module.test.dao;

import app.module.node.texts.BotText;
import jakarta.persistence.*;

@Entity
@Table(name = "test_question")
public class TestQuestion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "topic_id")
  private TestTopic topic;

  @ManyToOne
  @JoinColumn(name = "text_id")
  private BotText text;

  private Integer num;

  public TestQuestion() {
  }

  public Long getId() {
    return this.id;
  }

  public TestTopic getTopic() {
    return this.topic;
  }

  public BotText getText() {
    return this.text;
  }

  public Integer getNum() {
    return this.num;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setTopic(TestTopic topic) {
    this.topic = topic;
  }

  public void setText(BotText text) {
    this.text = text;
  }

  public void setNum(Integer num) {
    this.num = num;
  }
}