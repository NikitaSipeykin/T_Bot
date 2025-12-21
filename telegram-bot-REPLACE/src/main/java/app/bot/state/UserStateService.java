package app.bot.state;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStateService {

  private final UserStateRepository repository;

  public UserState getState(Long chatId) {
    return repository.findById(chatId)
        .map(UserStateEntity::getState)
        .orElse(UserState.DEFAULT);
  }

  public void setState(Long chatId, UserState state) {
    UserStateEntity entity = repository
        .findById(chatId)
        .orElseGet(() -> UserStateEntity.create(chatId));

    entity.setState(state);
    repository.save(entity);
  }


  public void reset(Long chatId) {
    setState(chatId, UserState.DEFAULT);
  }
}
