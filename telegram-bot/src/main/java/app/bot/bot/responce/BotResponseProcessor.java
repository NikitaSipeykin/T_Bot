package app.bot.bot.responce;

import app.bot.sender.TelegramSender;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class BotResponseProcessor {

  private final TelegramSender sender;
  private final TaskScheduler scheduler;

  public void process(BotResponse response) {
    if (response instanceof TextResponse text) {
      sender.send(text);
      return;
    }

    if (response instanceof MediaResponse media) {
      sender.send(media);
      return;
    }

    if (response instanceof HistoryResponse history) {
      sender.send(history);
      return;
    }

    if (response instanceof CompositeResponse composite) {
      sender.send(composite);
      return;
    }

    if (response instanceof SendWithDelayedResponse delayed) {
      sender.send(delayed.responsesNow());

      scheduler.schedule(
          () -> sender.send(delayed.responsesDelayed()),
          Instant.now().plus(delayed.delay())
      );
      return;
    }

    if (response instanceof SendInvoiceResponse invoice){
      sender.send(invoice);
      return;
    }

    throw new IllegalArgumentException(
        "Unsupported BotResponse type: " + response.getClass()
    );
  }
}

