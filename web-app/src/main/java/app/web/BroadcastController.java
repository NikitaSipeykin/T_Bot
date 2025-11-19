package app.web;

import app.core.BroadcastService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class BroadcastController {

  private final BroadcastService broadcastService;

  @GetMapping("/")
  public String index() {
    return "index"; // src/main/resources/templates/index.html
  }

  @PostMapping("/send")
  @ResponseBody
  public String send(@RequestBody BroadcastRequest request) {
    new Thread(() -> broadcastService.broadcast(request.getText())).start();
    return "ok";
  }
}
