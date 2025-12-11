package app.module.test;

import app.core.FinalMessage;
import app.core.OutgoingMessage;
import app.module.test.dao.TestResult;
import app.module.test.dao.TestTopic;
import app.module.test.repo.TestResultRepository;
import app.core.AnswerOption;
import app.module.test.dao.TestQuestion;
import app.module.test.repo.TestQuestionRepository;
import app.module.test.repo.TestTopicRepository;
import app.text.node.texts.BotTextService;
import app.text.node.texts.TextMarker;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TestService {

  private final TestStateService stateService;
  private final TestTopicRepository topicRepo;
  private final TestQuestionRepository questionRepo;
  private final TestResultRepository resultRepo;
  private final BotTextService text;

  private final Map<Long, List<String>> resultTopics = new HashMap<Long, List<String>>();

  public TestService(TestStateService stateService, TestTopicRepository topicRepo, TestQuestionRepository questionRepo,
                     TestResultRepository resultRepo, BotTextService text) {
    this.stateService = stateService;
    this.topicRepo = topicRepo;
    this.questionRepo = questionRepo;
    this.resultRepo = resultRepo;
    this.text = text;
  }

  public OutgoingMessage startTest(Long chatId) {
    TestState state = stateService.get(chatId);

    if (state == null) {
      state = new TestState();
      stateService.put(chatId, state);
      log.info("Создан новый TestState для chatId={}", chatId);

      state.setCurrentQuestionIndex(1);
      state.setCurrentTopicIndex(1);
    }
    return buildNextQuestion(chatId);
  }

  public OutgoingMessage buildNextQuestion(Long chatId) {
    TestState state = stateService.get(chatId);
    boolean isNextTopic = false;

    TestTopic topic = topicRepo
        .findByOrderIndex(state.getCurrentTopicIndex())
        .orElseThrow(() -> new IllegalStateException(
            "Topic not found for index: " + state.getCurrentTopicIndex()
        ));

    TestQuestion question = questionRepo
        .findByTopicIdAndNum(topic.getId(), state.getCurrentQuestionIndex())
        .orElseThrow(() -> new IllegalStateException(
            "Question not found: topic=" + topic.getId() + " num=" + state.getCurrentQuestionIndex()
        ));

    List<AnswerOption> options = List.of(
        new AnswerOption(1L, "Да", buildCallback(question.getId(), 1L)),
        new AnswerOption(2L, "Иногда", buildCallback(question.getId(), 2L)),
        new AnswerOption(3L, "Не уверен", buildCallback(question.getId(), 3L)),
        new AnswerOption(4L, "Нет", buildCallback(question.getId(), 4L))
    );

    if (state.getCurrentQuestionIndex() <= 1 && state.getCurrentTopicIndex() > 1) {
      isNextTopic = true;
    }

    return new OutgoingMessage(
        question.getText().getValue(),
        options, isNextTopic
    );
  }

  public Object processAnswer(Long chatId, String data) {
    String[] parts = data.split("_");
    Long qId = Long.valueOf(parts[2]);
    Long aId = Long.valueOf(parts[4]);

    TestQuestion question = questionRepo.findById(qId).orElseThrow();
    Long topicId = question.getTopic().getId();

    TestState state = stateService.get(chatId);

    double score = getScoreByAnswerId(aId);

    state.getTopicScores().merge(topicId, score, Double::sum);

    state.setCurrentQuestionIndex(state.getCurrentQuestionIndex() + 1);

    if (state.getCurrentQuestionIndex() > 3) {
      state.setCurrentQuestionIndex(1);
      state.setCurrentTopicIndex(state.getCurrentTopicIndex() + 1);
    }

    if (state.getCurrentTopicIndex() > 7) {
      return finishTest(chatId);
    } else {
      return buildNextQuestion(chatId);
    }
  }

  private FinalMessage finishTest(Long chatId) {
    TestState state = stateService.get(chatId);

    state.getTopicScores().forEach((topicId, score) -> {
      TestTopic topic = topicRepo.findById(topicId).orElseThrow();
      resultRepo.save(new TestResult(chatId, topic, score));
    });
    var scores = state.getTopicScores();

    boolean allZero = scores.values().stream().allMatch(score -> score == 0);

    if (allZero) {
      stateService.reset(chatId);
      log.info("finishTest - ALL_ZERO");
      return new FinalMessage(text.format(TextMarker.ALL_ZERO), null);
    }

    List<Map.Entry<Long, Double>> nonZeroTopics = scores.entrySet().stream()
        .filter(e -> e.getValue() > 0)
        .toList();

    if (nonZeroTopics.size() == 1) {
      var t = nonZeroTopics.get(0);
      TestTopic topic = topicRepo.findById(t.getKey()).orElseThrow();
      String msg = String.format("Я проанализировал твои ответы и обнаружил, что энергия застряла в:" +
                                 "\n\n• %s — %.1f\n\n" +
                                 "Это единственный дисбаланс ❤️",
          topic.getName(), t.getValue()
      );

      stateService.reset(chatId);
      return new FinalMessage(msg, List.of(topic.getName()));
    }

    var top2 = state.getTopicScores().entrySet().stream().sorted((a, b)
        -> Double.compare(b.getValue(), a.getValue())).limit(2).toList();

    String msg = String.format("Я проанализировал твои ответы и исходя из них, энергия застряла в: \n" +
                               "1) %s — %.1f\n" +
                               "2) %s — %.1f\n" +
                               "Не переживай, есть решения!",
        topicRepo.findById(top2.get(0).getKey()).get().getName(), top2.get(0).getValue(),
        topicRepo.findById(top2.get(1).getKey()).get().getName(), top2.get(1).getValue());

    stateService.reset(chatId);
    return new FinalMessage(msg, List.of(
        topicRepo.findById(top2.get(0).getKey()).get().getName(),
        topicRepo.findById(top2.get(1).getKey()).get().getName()));
  }

  private String buildCallback(Long qId, Long aId) {
    return "TEST_Q_" + qId + "_A_" + aId;
  }

  private double getScoreByAnswerId(Long aId) {
    return switch (aId.intValue()) {
      case 1 -> 1.0;
      case 2, 3 -> 0.5;
      case 4 -> 0;
      default -> throw new IllegalArgumentException("Unknown answer id: " + aId);
    };
  }

  public void saveResultTopics(Long chatId, List<String> names) {
    resultTopics.put(chatId, names);
    log.info("resultTopics save = " + resultTopics);
  }

  public List<String> getResultTopics(Long chatId) {
    log.info("resultTopics get = " + resultTopics);
    return resultTopics.getOrDefault(chatId, List.of());

  }
}