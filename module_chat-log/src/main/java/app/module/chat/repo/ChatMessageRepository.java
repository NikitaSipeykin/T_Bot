package app.module.chat.repo;

import app.module.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository
    extends JpaRepository<ChatMessage, Long> {

  List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

  Page<ChatMessage> findByConversationIdOrderByCreatedAtAsc(
      Long conversationId,
      Pageable pageable
  );

  @Query("""
        select m from ChatMessage m
        where m.messageText ilike %:query%
    """)
  List<ChatMessage> search(@Param("query") String query);

  List<ChatMessage> findFirst37ByChatIdOrderByIdAsc(Long chatId);
}

