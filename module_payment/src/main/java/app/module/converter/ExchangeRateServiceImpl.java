package app.module.converter;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

  private static final String BASE = "USD";
  private static final Set<String> SUPPORTED = Set.of("RUB", "EUR", "UZS");

  private final WebClient webClient;

  // кеш курсов: RUB -> 93.45
  private final Map<String, BigDecimal> rates = new ConcurrentHashMap<>();

  public ExchangeRateServiceImpl(WebClient.Builder builder) {
    this.webClient = builder.baseUrl("https://open.er-api.com").build();
  }

  /**
   * Обновляем курсы при старте приложения
   */
  @PostConstruct
  public void init() {
    updateRates();
  }

  /**
   * Обновляем курсы каждые 6 часов
   */
  @Scheduled(cron = "0 0 */6 * * *")
  public void updateRates() {
    try {
      log.info("Updating exchange rates...");

      JsonNode response = webClient.get()
          .uri("/v6/latest/{base}", BASE)
          .retrieve()
          .bodyToMono(JsonNode.class)
          .block();

      JsonNode ratesNode = response.get("rates");

      for (String currency : SUPPORTED) {
        BigDecimal rate = ratesNode.get(currency).decimalValue();
        rates.put(currency, rate);
      }

      log.info("Exchange rates updated: {}", rates);

    } catch (Exception e) {
      log.error("Failed to update exchange rates, using cached values", e);
    }
  }

  @Override
  public int convertFromUsd(int amountMinor, String toCurrency) {
    BigDecimal rate = rates.get(toCurrency);

    if (rate == null) {
      throw new IllegalStateException("Currency not supported: " + toCurrency);
    }

    // amountMinor = 10000 → 100.00 USD
    BigDecimal amountUsd = BigDecimal.valueOf(amountMinor)
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    BigDecimal converted = amountUsd.multiply(rate);

    // обратно в minor units
    return converted
        .multiply(BigDecimal.valueOf(100))
        .setScale(0, RoundingMode.HALF_UP)
        .intValueExact();
  }
}
