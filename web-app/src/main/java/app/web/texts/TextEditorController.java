package app.web.texts;

import app.text.node.texts.BotText;
import app.text.node.texts.BotTextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/texts")
@RequiredArgsConstructor
public class TextEditorController {

  private final BotTextRepository repository;
  private final TextSyncService syncService;

  @GetMapping
  public List<BotText> getAll() {
    return repository.findAll();
  }

  @PutMapping("/{id}")
  public BotText update(
      @PathVariable String id,
      @RequestBody BotText updated
  ) {
    BotText text = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Not found"));

    text.setValue(updated.getValue());
    repository.save(text);

    // отправляем в бота
    syncService.sendToBot(text);

    return text;
  }
}
