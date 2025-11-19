package app.core;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BroadcastService {

  private final SubscriberService subscriberService;
  private final MessageSender sender;

  public BroadcastService(SubscriberService subscriberService, MessageSender sender) {
    this.subscriberService = subscriberService;
    this.sender = sender;
  }

  public void broadcast(String text) {
    List<Long> ids = subscriberService.getActiveSubscribers();
    for (Long id : ids) {
      try {
        sender.send(id, text);
      } catch (Exception e) {
        // логирование, retry и т.д. — можно добавить
      }
    }
  }
}
