package app.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ChatWebController {

  @GetMapping("/chats")
  public String chatsPage() {
    return "chats.html";
  }

  @GetMapping("/chats/{conversationId}")
  public String chatDetailsPage(@PathVariable Long conversationId) {
    return "chat-details.html";
  }
}

