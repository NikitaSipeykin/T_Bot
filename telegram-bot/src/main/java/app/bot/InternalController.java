package app.bot;

import app.text.node.texts.BotText;
import app.text.node.texts.BotTextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalController {
  private final BotTextRepository repository;
  private final BaseTelegramBot bot;

  @PostMapping("/send")
  public String internalSend(@RequestBody InternalSendRequest req) {
    bot.sendMessage(req.getChatId(), req.getText(), null);
    return "ok";
  }

  @PostMapping("/update-text")
  public String update(@RequestBody BotText text) {

    repository.save(text);
    System.out.println("Updated bot text: " + text.getId());

    return "ok";
  }
}
