package app.core.broadcast;

import app.core.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    subscriberService.getActiveSubscribers()
        .forEach(chatId ->
            sender.sendText(chatId, text)
        );
  }
}
