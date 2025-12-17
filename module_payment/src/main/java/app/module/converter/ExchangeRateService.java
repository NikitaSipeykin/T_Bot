package app.module.converter;

public interface ExchangeRateService {

  /**
   * @param amountMinor сумма в minor units (например, 10000 = 100.00 USD)
   * @param toCurrency  целевая валюта (RUB, EUR, UZS)
   * @return сумма в minor units целевой валюты
   */
  int convertFromUsd(int amountMinor, String toCurrency);
}

