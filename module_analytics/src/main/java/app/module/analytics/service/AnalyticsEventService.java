package app.module.analytics.service;

import app.module.analytics.dto.AnalyticsEventCreateDto;
import app.module.analytics.model.AnalyticsEventEntity;
import app.module.analytics.repository.AnalyticsEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AnalyticsEventService {

  private final AnalyticsEventRepository repository;

  public void track(AnalyticsEventCreateDto dto) {
    AnalyticsEventEntity entity = AnalyticsEventEntity.builder()
        .chatId(dto.getChatId())
        .eventType(dto.getEventType())
        .blockName(dto.getBlockName())
        .buttonText(dto.getButtonText())
        .paymentId(dto.getPaymentId())
        .paymentStatus(dto.getPaymentStatus())
        .paymentErrorCode(dto.getPaymentErrorCode())
        .metadata(dto.getMetadata())
        .createdAt(Instant.now())
        .build();

    repository.save(entity);
  }
}

