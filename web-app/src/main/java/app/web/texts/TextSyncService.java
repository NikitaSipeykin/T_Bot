package app.web.texts;

import app.text.node.texts.BotText;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TextSyncService {

  private final RestTemplate rest = new RestTemplate();

  @Value("${bot.internal.update-text-url}")
  private String updateUrl; // http://localhost:8081/internal/update-text

  public void sendToBot(BotText text) {
    try {
      rest.postForObject(updateUrl, text, String.class);
    } catch (Exception e) {
      System.out.println("Error syncing with bot: " + e.getMessage());
    }
  }
}
