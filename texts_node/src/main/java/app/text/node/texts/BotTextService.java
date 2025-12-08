package app.text.node.texts;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotTextService {
  private static final Logger log = LoggerFactory.getLogger(BotTextService.class);
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
