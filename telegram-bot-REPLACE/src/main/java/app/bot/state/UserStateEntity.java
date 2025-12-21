package app.bot.state;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserStateEntity {

  @Id
  private Long chatId;

  private String state;

  protected UserStateEntity() {}

  public UserStateEntity(Long chatId, UserState state) {
    this.chatId = chatId;
    this.state = state.name();
  }

  public Long getChatId() {
    return chatId;
  }

  public static UserStateEntity create(Long chatId) {
    return new UserStateEntity(chatId, UserState.DEFAULT);
  }

  public UserState getState() {
    return UserState.valueOf(state);
  }

  public void setState(UserState state) {
    this.state = state.name();
  }
}
