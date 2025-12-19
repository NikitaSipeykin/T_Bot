package app.module.test;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TestStateService {
  private final Map<Long, TestState> states = new ConcurrentHashMap();

  public TestStateService() {
  }

  public TestState getOrCreate(Long chatId) {
    return (TestState)this.states.computeIfAbsent(chatId, (id) -> {
      return new TestState();
    });
  }

  public TestState get(Long chatId) {
    return (TestState)this.states.get(chatId);
  }

  public void reset(Long chatId) {
    this.states.remove(chatId);
  }

  public void clearAll() {
    this.states.clear();
  }

  public void put(Long chatId, TestState state) {
    this.states.put(chatId, state);
  }
}
