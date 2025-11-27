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

  public void saveEmail(Long chatId, String email) {
    jdbc.update(
        "UPDATE subscribers SET email = ? WHERE chat_id = ?",
        email, chatId
    );
  }

  public void setCode(Long chatId, String code) {
    jdbc.update(
        "UPDATE subscribers SET verification_code = ? WHERE chat_id = ?",
        code, chatId
    );
  }

  public String getVerificationCode(Long chatId) {
    return jdbc.queryForObject(
        "SELECT verification_code FROM subscribers WHERE chat_id = ?",
        String.class,
        chatId
    );
  }

  public void verify(long chatId){
    jdbc.update(
        "UPDATE subscribers SET verified = true WHERE chat_id = ?",
         chatId
    );
  }
}
