package app.core.broadcast;

import app.core.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BroadcastService {
  private final SubscriberService subscriberService;
  private final MessageSender sender;

  public BroadcastService(SubscriberService subscriberService, MessageSender sender) {
    this.subscriberService = subscriberService;
    this.sender = sender;
  }

  public void broadcast(String text) {
    log.debug("broadcast()");
    subscriberService.getActiveSubscribers()
        .forEach(chatId ->
            sender.sendText(chatId, text)
        );
  }
}
