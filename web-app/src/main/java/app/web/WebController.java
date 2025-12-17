package app.web;

import app.core.broadcast.BroadcastService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class WebController {

  private final BroadcastService broadcastService;

  @GetMapping("/")
  public String index() {
    return "index";
  }

  @GetMapping("/texts")
  public String textsPage() {
    return "edit-texts.html";
  }

  @GetMapping("/broadcast")
  public String broadcastPage() {
    return "index";
  }

  @GetMapping("/media")
  public String mediaPage() {
    return "upload-videos.html";
  }

  @PostMapping("/send")
  @ResponseBody
  public String send(@RequestBody BroadcastRequest request) {
    new Thread(() -> broadcastService.broadcast(request.getText())).start();
    return "ok";
  }
}
