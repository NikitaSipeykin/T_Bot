package app.module.chat.service;

import app.module.chat.SenderType;
import app.module.chat.entity.ChatConversation;
import app.module.chat.entity.ChatMessage;
import app.module.chat.repo.ChatMessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ChatHistoryServiceImpl
    implements ChatHistoryService {

  private final ChatConversationService conversationService;
  private final ChatMessageRepository messageRepository;

  public ChatHistoryServiceImpl(
      ChatConversationService conversationService,
      ChatMessageRepository messageRepository) {

    this.conversationService = conversationService;
    this.messageRepository = messageRepository;
  }

  @Override
  public ChatMessage logUserMessage(Long chatId, String text) {

    ChatConversation conversation =
        conversationService.getOrCreateActive(chatId);

    ChatMessage message = new ChatMessage();
    message.setConversation(conversation);
    message.setChatId(chatId);
    message.setSenderType(SenderType.USER);
    message.setMessageText(text);

    return messageRepository.save(message);
  }

  @Override
  public ChatMessage logBotMessage(Long chatId, String text) {

    ChatConversation conversation =
        conversationService.getOrCreateActive(chatId);

    ChatMessage message = new ChatMessage();
    message.setConversation(conversation);
    message.setChatId(chatId);
    message.setSenderType(SenderType.BOT);
    message.setMessageText(text);

    return messageRepository.save(message);
  }
}

