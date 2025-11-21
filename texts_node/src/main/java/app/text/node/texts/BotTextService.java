package app.text.node.texts;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotTextService {

  private final BotTextRepository repo;

  public String get(String key) {
    return repo.findById(key)
        .map(BotText::getValue)
        .orElse("Текст не найден: " + key);
  }

  public String format(String key, Object... args) {
    return String.format(get(key), args);
  }
}
