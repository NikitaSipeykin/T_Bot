package app.module.chat.service;

import app.module.chat.entity.ChatConversation;

public interface ChatConversationService {

  ChatConversation getOrCreateActive(Long chatId);

  void closeConversation(Long chatId);
}
