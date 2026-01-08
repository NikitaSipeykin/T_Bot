package app.bot.internal;

import app.bot.sender.TelegramMessageSender;
import app.core.broadcast.BroadcastService;
import app.module.node.texts.BotText;
import app.module.node.texts.BotTextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
@Slf4j
public class InternalController {

  private final BroadcastService broadcastService;
  private final BotTextRepository repository;

  @PostMapping("/send")
  public String broadcast(@RequestBody InternalSendRequest req) {
    broadcastService.broadcast(req.getText());
    return "ok";
  }

  @PostMapping("/update-module")
  public String update(@RequestBody BotText text) {
    repository.save(text);
    log.debug("Updated bot module: {}", text.getId());
    return "ok";
  }
}
