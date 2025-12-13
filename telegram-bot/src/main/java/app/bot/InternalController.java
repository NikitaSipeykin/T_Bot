package app.bot;

import app.module.node.texts.BotText;
import app.module.node.texts.BotTextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
@Slf4j
public class InternalController {
  private final BotTextRepository repository;
  private final BaseTelegramBot bot;

  @PostMapping("/send")
  public String internalSend(@RequestBody InternalSendRequest req) {
    bot.sendMessage(req.getChatId(), req.getText(), null);
    return "ok";
  }

  @GetMapping("/daily_update")
  public void launchDailyUpdate(){
    log.info("launchDailyUpdate");
    bot.scheduledDailyUpdate();
  }

  @PostMapping("/update-module")
  public String update(@RequestBody BotText text) {

    repository.save(text);
    System.out.println("Updated bot module: " + text.getId());

    return "ok";
  }
}
