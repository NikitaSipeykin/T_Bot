package app.web.controller;

import app.module.chat.dto.ChatMessageDto;
import app.module.chat.dto.ConversationListItemDto;
import app.module.chat.service.AdminChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/chats")
@RequiredArgsConstructor
public class AdminChatController {

  private final AdminChatService chatService;

  @GetMapping
  public List<ConversationListItemDto> chats() {
    return chatService.getAllChats();
  }

  @GetMapping("/{id}/messages")
  public List<ChatMessageDto> messages(@PathVariable Long id) {
    return chatService.getMessages(id);
  }
}

