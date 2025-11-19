package app.core;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubscriberRepository {
  private final JdbcTemplate jdbc;

  public SubscriberRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public void saveOrActivate(Long chatId, String username, String firstName) {
    jdbc.update("INSERT INTO subscribers(chat_id, username, first_name, active) VALUES (?, ?, ?, true) " +
                "ON CONFLICT (chat_id) DO UPDATE SET active = TRUE, username = excluded.username, first_name = excluded.first_name",
        chatId, username, firstName);
  }

  public void deactivate(Long chatId) {
    jdbc.update("UPDATE subscribers SET active = FALSE WHERE chat_id = ?", chatId);
  }

  public List<Long> findActiveChatIds() {
    return jdbc.queryForList("SELECT chat_id FROM subscribers WHERE active = TRUE", Long.class);
  }
}
