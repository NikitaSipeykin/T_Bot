package app.bot.facade;

import app.module.analytics.dto.AnalyticsEventCreateDto;
import app.module.analytics.model.AnalyticsEventType;
import app.module.analytics.service.AnalyticsEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AnalyticsFacade {

  private final AnalyticsEventService service;

    /* =========================
       SUBSCRIBE
       ========================= */

  public void trackSubscribe(Message message, boolean isReturning) {
    trackSafe(
        AnalyticsEventCreateDto.builder()
            .chatId(message.getChatId())
            .eventType(AnalyticsEventType.SUBSCRIBE.name())
            .metadata(Map.of(
                "is_returning", isReturning,
                "source", "start_command"
            ))
            .build()
    );
  }

    /* =========================
       BLOCK VIEW
       ========================= */

  public void trackBlockView(Long chatId, String blockName) {
    trackSafe(
        AnalyticsEventCreateDto.builder()
            .chatId(chatId)
            .eventType(AnalyticsEventType.BLOCK_VIEW.name())
            .blockName(blockName)
            .build()
    );
  }

    /* =========================
       PAYMENT START
       ========================= */

  public void trackPaymentStart(Long chatId, Long paymentId, int amount) {
    trackSafe(
        AnalyticsEventCreateDto.builder()
            .chatId(chatId)
            .eventType(AnalyticsEventType.PAYMENT_START.name())
            .paymentId(paymentId)
            .metadata(Map.of(
                "amount", amount
            ))
            .build()
    );
  }

    /* =========================
       INTERNAL
       ========================= */

  private void trackSafe(AnalyticsEventCreateDto dto) {
    try {
      service.track(dto);
    } catch (Exception e) {
      // аналитика не должна ломать бизнес
    }
  }
}

