package app.module.analytics.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "bot_analytics_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsEventEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "chat_id")
  private Long chatId;

  @Column(name = "event_type", nullable = false)
  private String eventType;

  @Column(name = "block_name")
  private String blockName;

  @Column(name = "button_text")
  private String buttonText;

  @Column(name = "payment_id")
  private Long paymentId;

  @Column(name = "payment_status")
  private String paymentStatus;

  @Column(name = "payment_error_code")
  private String paymentErrorCode;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> metadata;


  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;
}

