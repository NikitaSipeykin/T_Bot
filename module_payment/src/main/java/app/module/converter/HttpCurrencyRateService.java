package app.module.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class HttpCurrencyRateService implements CurrencyRateService {

  private final RestTemplate restTemplate = new RestTemplate();

  @Override
  public int getRate(String from, String to) {
    // временно хардкод, дальше заменим
    if (from.equals("USD") && to.equals("RUB")) {
      return 9234; // 92.34
    }

    throw new IllegalStateException(
        "Rate not supported: " + from + " -> " + to
    );
  }
}

