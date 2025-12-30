package app.module.chat.repo;

import app.module.chat.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatConversationRepository
    extends JpaRepository<ChatConversation, Long> {

  Optional<ChatConversation> findByChatIdAndClosedAtIsNull(Long chatId);

  @Query("""
    select c
    from ChatConversation c
    order by c.createdAt desc
  """)
  List<ChatConversation> findAllOrderByCreatedAtDesc();
}

