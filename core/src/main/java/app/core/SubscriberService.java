package app.core;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriberService {
  private final SubscriberRepository repo;

  public SubscriberService(SubscriberRepository repo) {
    this.repo = repo;
  }

  public void subscribe(Long chatId, String username, String firstName) {
    repo.saveOrActivate(chatId, username, firstName);
  }

  public void unsubscribe(Long chatId) {
    repo.deactivate(chatId);
  }

  public List<Long> getActiveSubscribers() {
    return repo.findActiveChatIds();
  }

  public void setEmail(Long chatId, String email){
    repo.saveEmail(chatId, email);
  }

  public void setCode(Long chatId, String code){
    repo.setCode(chatId, code);
  }

  public String getCode(Long chatId){
    return repo.getVerificationCode(chatId);
  }

  public void setVerified(Long chatId){
    repo.verify(chatId);
  }
}
