package app.module.chat.service;

import app.module.chat.entity.ChatMessage;

public interface ChatHistoryService {

  ChatMessage logUserMessage(Long chatId, String text);

  ChatMessage logBotMessage(Long chatId, String text);
}
