package app.module.chat.service;

import app.module.chat.SenderType;
import app.module.chat.entity.ChatConversation;
import app.module.chat.entity.ChatMessage;
import app.module.chat.repo.ChatMessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

  public String buildChatHistory(Long chatId) {
    List<ChatMessage> messages = messageRepository.findFirst37ByChatIdOrderByIdAsc(chatId);

    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    StringBuilder sb = new StringBuilder();

    for (ChatMessage msg : messages) {
      sb.append("[")
          .append(msg.getCreatedAt())
          .append("] ")
          .append(msg.getSenderType())
          .append(":\n")
          .append(msg.getMessageText())
          .append("\n\n");
    }

    return sb.toString();
  }

  public File writeToFile(Long chatId, String content) throws IOException {
    File file = new File("chat_" + chatId + ".txt");

    try (Writer writer = new BufferedWriter(
        new OutputStreamWriter(
            new FileOutputStream(file), StandardCharsets.UTF_8))) {
      writer.write(content);
    }

    return file;
  }

}

