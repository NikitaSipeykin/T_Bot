package app.bot.facade;

import app.module.analytics.dto.AnalyticsEventCreateDto;
import app.module.analytics.model.AnalyticsEventType;
import app.module.analytics.service.AnalyticsEventService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsFacade {

  private final AnalyticsEventService service;

      /* =========================
       TEST
       ========================= */

  public void trackTestStart(Long chatId) {
    trackSafe(
        AnalyticsEventCreateDto.builder()
            .chatId(chatId)
            .eventType("TEST_START")
            .metadata(Map.of(
                "test", "chakra"
            ))
            .build()
    );
  }

  public void trackTestQuestionShown(Long chatId, String questionTopic) {
    trackSafe(
        AnalyticsEventCreateDto.builder()
            .chatId(chatId)
            .eventType("TEST_QUESTION_SHOWN")
            .metadata(Map.of(
                "question_topic", questionTopic
            ))
            .build()
    );
  }

  public void trackTestAlreadyFinished(Long chatId) {
    trackSafe(
        AnalyticsEventCreateDto.builder()
            .chatId(chatId)
            .eventType("TEST_ALREADY_FINISHED")
            .blockName(TextMarker.TEST_END_ALREADY)
            .build()
    );
  }

  public void trackTestAnswer(Long chatId, String answerCode) {
    trackSafe(
        AnalyticsEventCreateDto.builder()
            .chatId(chatId)
            .eventType("TEST_ANSWER_SUBMIT")
            .metadata(Map.of(
                "answer", answerCode
            ))
            .build()
    );
  }

  public void trackTestFinish(Long chatId, int topicsCount) {
    trackSafe(
        AnalyticsEventCreateDto.builder()
            .chatId(chatId)
            .eventType("TEST_FINISH")
            .metadata(Map.of(
                "topics_count", topicsCount
            ))
            .build()
    );
  }

  public void trackZeroResult(Long chatId) {
    trackSafe(
        AnalyticsEventCreateDto.builder()
            .chatId(chatId)
            .eventType("TEST_ZERO_RESULT")
            .build()
    );
  }

    /* =========================
       TEST RESULT
       ========================= */

  public void trackCtaShown(Long chatId, String cta) {
    trackSafe(
        AnalyticsEventCreateDto.builder()
            .chatId(chatId)
            .eventType("CTA_SHOWN")
            .metadata(Map.of(
                "cta", cta,
                "source", "test_result"
            ))
            .build()
    );
  }

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

  public void trackBlockView(Long chatId, String blockName, Map<String, Object> meta) {
    trackSafe(
        AnalyticsEventCreateDto.builder()
            .chatId(chatId)
            .eventType(AnalyticsEventType.BLOCK_VIEW.name())
            .blockName(blockName)
            .metadata(meta)
            .build()
    );
  }

        /* =========================
       BUTTON CLICK
       ========================= */

  public void trackButtonClick(CallbackQuery cq, String button) {
    trackSafe(
        AnalyticsEventCreateDto.builder()
            .chatId(cq.getMessage().getChatId())
            .eventType(AnalyticsEventType.BUTTON_CLICK.name())
            .buttonText(button)
            .metadata(Map.of(
                "source", "callback"
            ))
            .build()
    );
  }

      /* =========================
       UNSUBSCRIBE
       ========================= */

  public void trackUnsubscribe(Message message) {
    trackSafe(
        AnalyticsEventCreateDto.builder()
            .chatId(message.getChatId())
            .eventType(AnalyticsEventType.UNSUBSCRIBE.name())
            .metadata(Map.of(
                "source", "unsubscribe_command",
                "reason", "manual"
            ))
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

  public void trackPaymentUnavailable(Long chatId, String currency, String reason) {
    trackSafe(
        AnalyticsEventCreateDto.builder()
            .chatId(chatId)
            .eventType("PAYMENT_UNAVAILABLE")
            .metadata(Map.of(
                "currency", currency,
                "reason", reason
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
      log.error("Can't track analytics message. ");
    }
  }
}

