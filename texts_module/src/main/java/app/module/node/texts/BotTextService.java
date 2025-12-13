package app.module.node.texts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotTextService {
  private final BotTextRepository repo;

  public String get(String key) {
    log.info("key = " + key);
    return repo.findById(key)
        .map(BotText::getValue)
        .orElse("Текст не найден: " + key);
  }

  public String format(String key, Object... args) {
    return String.format(get(key), args);
  }
}
