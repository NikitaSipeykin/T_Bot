package app.module.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class CachedCurrencyRateService implements CurrencyRateService {

  private final CurrencyRateService delegate;

  private final Map<String, Integer> cache = new ConcurrentHashMap<>();

  public CachedCurrencyRateService(HttpCurrencyRateService delegate) {
    this.delegate = delegate;
  }

  @Override
  public int getRate(String from, String to) {
    String key = from + "_" + to;

    return cache.computeIfAbsent(key, k -> {
      int rate = delegate.getRate(from, to);
      log.info("Fetched rate {} -> {} = {}", from, to, rate);
      return rate;
    });
  }

  @Scheduled(cron = "0 */1 * * * *") // каждые 30 минут
  public void refreshRates() {
    cache.clear();
  }

}

