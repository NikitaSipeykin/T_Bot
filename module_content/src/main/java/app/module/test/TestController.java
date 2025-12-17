package app.module.test;

import org.springframework.stereotype.Component;

@Component
public class TestController {
  private final TestService testService;

  public TestController(TestService testService) {
    this.testService = testService;
  }

  public void start(Long chatId) {
    this.testService.startTest(chatId);
  }

  public void submitAnswer(Long chatId, String data) {
    this.testService.processAnswer(chatId, data);
  }
}
