package app.bot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
public class InternalController {

  private final BaseTelegramBot bot;

  public InternalController(BaseTelegramBot bot) {
    this.bot = bot;
  }

  @PostMapping("/send")
  public String internalSend(@RequestBody InternalSendRequest req) {
    bot.sendMessage(req.getChatId(), req.getText(), null);
    return "ok";
  }
}
