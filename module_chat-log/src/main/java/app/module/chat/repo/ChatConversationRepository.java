package app.module.chat.repo;

import app.module.chat.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatConversationRepository
    extends JpaRepository<ChatConversation, Long> {

  Optional<ChatConversation> findByChatIdAndClosedAtIsNull(Long chatId);
}

