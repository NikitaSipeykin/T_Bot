package app.module.chat.service;

import app.module.chat.entity.ChatConversation;
import app.module.chat.repo.ChatConversationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Transactional
public class ChatConversationServiceImpl
    implements ChatConversationService {

  private final ChatConversationRepository conversationRepository;

  public ChatConversationServiceImpl(
      ChatConversationRepository conversationRepository) {
    this.conversationRepository = conversationRepository;
  }

  @Override
  public ChatConversation getOrCreateActive(Long chatId) {
    return conversationRepository
        .findByChatIdAndClosedAtIsNull(chatId)
        .orElseGet(() -> {
          ChatConversation c = new ChatConversation();
          c.setChatId(chatId);
          return conversationRepository.save(c);
        });
  }

  @Override
  public void closeConversation(Long chatId) {
    conversationRepository
        .findByChatIdAndClosedAtIsNull(chatId)
        .ifPresent(c -> {
          c.setClosedAt(Instant.now());
        });
  }
}

