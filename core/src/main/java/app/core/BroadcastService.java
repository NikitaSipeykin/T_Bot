package app.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BroadcastService {
  private static final Logger log = LoggerFactory.getLogger(BroadcastService.class);
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
        sender.sendText(id, text);
      } catch (Exception e) {
        log.error("Ошибка при попытке отправить рассылку!" + e);
      }
    }
  }
}
