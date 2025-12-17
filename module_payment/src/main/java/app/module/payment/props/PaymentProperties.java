package app.module.payment.props;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@ConfigurationProperties(prefix = "bot.payments")
public class PaymentProperties {
  private Map<String, Provider> providers = new HashMap<>();
  private int basePrice;

  public PaymentOption resolve(String currency) {
    if (currency == null || currency.isBlank()) {
      throw new IllegalArgumentException("Currency must not be null or blank");
    }

    return providers.entrySet().stream()
        .filter(e -> e.getValue().currencies().contains(currency))
        .findFirst()
        .map(e -> new PaymentOption(
            e.getKey(),
            e.getValue().token(),
            currency,
            basePrice
        ))
        .orElseThrow(() -> new IllegalStateException(
            "No payment provider supports currency=" + currency
        ));
  }

  public Map<String, Provider> getProviders() {
    return providers;
  }

  public int getBasePrice() {
    return basePrice;
  }

  public void setBasePrice(int basePrice) {
    this.basePrice = basePrice;
  }

  public void setProviders(Map<String, Provider> providers) {
    this.providers = providers;
  }
}

