package app.module.chat.service;

import app.core.broadcast.SubscriberRepository;
import app.module.chat.dto.ChatMessageDto;
import app.module.chat.dto.ConversationListItemDto;
import app.module.chat.entity.ChatMessage;
import app.module.chat.repo.ChatConversationRepository;
import app.module.chat.repo.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminChatService {

  private final ChatConversationRepository conversationRepository;
  private final ChatMessageRepository messageRepository;
  private final SubscriberRepository subscriberRepository;

  // ===== СПИСОК ЧАТОВ =====
  public List<ConversationListItemDto> getAllChats() {

    return conversationRepository.findAllOrderByCreatedAtDesc()
        .stream()
        .map(c -> {
          ConversationListItemDto dto = new ConversationListItemDto();
          dto.setConversationId(c.getId());
          dto.setChatId(c.getChatId());
          dto.setUsername(subscriberRepository.findDisplayNameByChatId(c.getChatId()));

          List<ChatMessage> messages = c.getMessages();
          if (!messages.isEmpty()) {
            ChatMessage last = messages.get(messages.size() - 1);
            dto.setLastMessageText(last.getMessageText());
            dto.setLastMessageAt(last.getCreatedAt());
          }

          return dto;
        })
        .toList();
  }

  // ===== СООБЩЕНИЯ ЧАТА =====
  public List<ChatMessageDto> getMessages(Long conversationId) {

    return messageRepository
        .findByConversationIdOrderByCreatedAtAsc(conversationId)
        .stream()
        .map(m -> {
          ChatMessageDto dto = new ChatMessageDto();
          dto.senderType = m.getSenderType();
          dto.text = m.getMessageText();
          dto.createdAt = m.getCreatedAt();

          return dto;
        })
        .toList();
  }
}
