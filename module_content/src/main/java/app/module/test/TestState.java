package app.module.test;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TestState {

  private int currentTopicIndex = 1;
  private int currentQuestionIndex = 1;

  // topicId → score
  private final Map<Long, Double> topicScores = new HashMap<>();

  public void incrementTopic() {
    currentTopicIndex++;
    currentQuestionIndex = 1;
  }

  public void incrementQuestion() {
    currentQuestionIndex++;
  }

  public void resetQuestion() {
    currentQuestionIndex = 0;
  }

  public void addScore(Long topicId, double score) {
    topicScores.merge(topicId, score, Double::sum);
  }
}
