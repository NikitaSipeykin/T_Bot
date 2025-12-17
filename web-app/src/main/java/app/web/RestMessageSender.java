package app.web;

import app.core.MessageSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestMessageSender implements MessageSender {

  private final RestTemplate rest = new RestTemplate();
  private final String botInternalUrl; // e.g. http://localhost:8080/internal/send

  public RestMessageSender(@Value("${bot.internal.url}") String botInternalUrl) {
    this.botInternalUrl = botInternalUrl;
  }

  @Override
  public void sendText(Long chatId, String text) {
    String url = botInternalUrl;

    var payload = new java.util.HashMap<String, Object>();
    payload.put("chatId", chatId);
    payload.put("text", text);

    rest.postForObject(url, payload, String.class);
  }
}
