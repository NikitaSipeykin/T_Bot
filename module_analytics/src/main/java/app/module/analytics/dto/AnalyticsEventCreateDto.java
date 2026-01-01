package app.module.analytics.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class AnalyticsEventCreateDto {

  private Long chatId;
  private String eventType;

  private String blockName;
  private String buttonText;

  private Long paymentId;
  private String paymentStatus;
  private String paymentErrorCode;

  private Map<String, Object> metadata;
}

