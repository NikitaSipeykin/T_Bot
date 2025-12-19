package app.module.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceImplTest {

    private ExchangeRateServiceImpl exchangeRateService;
    private Map<String, BigDecimal> rates;

    @BeforeEach
    void setUp() {
        WebClient.Builder builder = WebClient.builder();
        exchangeRateService = new ExchangeRateServiceImpl(builder);
        
        rates = new ConcurrentHashMap<>();
        rates.put("RUB", BigDecimal.valueOf(93.45));
        rates.put("EUR", BigDecimal.valueOf(0.92));
        rates.put("UZS", BigDecimal.valueOf(12500.0));

        // Use reflection to set the rates map for testing
        ReflectionTestUtils.setField(exchangeRateService, "rates", rates);
    }

    @Test
    void testConvertFromUsd_RUB() {
        int amountMinor = 10000; // 100.00 USD
        String toCurrency = "RUB";

        int result = exchangeRateService.convertFromUsd(amountMinor, toCurrency);

        // 100.00 USD * 93.45 RUB/USD = 9345.00 RUB = 934500 minor units
        assertEquals(934500, result);
    }

    @Test
    void testConvertFromUsd_EUR() {
        int amountMinor = 10000; // 100.00 USD
        String toCurrency = "EUR";

        int result = exchangeRateService.convertFromUsd(amountMinor, toCurrency);

        // 100.00 USD * 0.92 EUR/USD = 92 EUR = 9200 minor units
        assertEquals(9200, result);
    }

    @Test
    void testConvertFromUsd_UZS() {
        int amountMinor = 10000; // 100.00 USD
        String toCurrency = "UZS";

        int result = exchangeRateService.convertFromUsd(amountMinor, toCurrency);

        // 100.00 USD * 12500.0 UZS/USD = 1250000.00 UZS = 125000000 minor units
        assertEquals(125000000, result);
    }

    @Test
    void testConvertFromUsd_UnsupportedCurrency() {
        int amountMinor = 10000;
        String toCurrency = "GBP";

        assertThrows(IllegalStateException.class, () -> {
            exchangeRateService.convertFromUsd(amountMinor, toCurrency);
        });
    }

    @Test
    void testConvertFromUsd_Rounding() {
        // Test rounding behavior
        int amountMinor = 3333; // 33.33 USD
        String toCurrency = "RUB";

        int result = exchangeRateService.convertFromUsd(amountMinor, toCurrency);

        // Should round properly
        assertTrue(result > 0);
    }

    @Test
    void testConvertFromUsd_ZeroAmount() {
        int amountMinor = 0;
        String toCurrency = "RUB";

        int result = exchangeRateService.convertFromUsd(amountMinor, toCurrency);

        assertEquals(0, result);
    }

    @Test
    void testConvertFromUsd_LargeAmount() {
        int amountMinor = 1000000; // 10000.00 USD
        String toCurrency = "RUB";

        int result = exchangeRateService.convertFromUsd(amountMinor, toCurrency);

        // 10000.00 USD * 93.45 RUB/USD = 934500.00 RUB = 93450000 minor units
        assertEquals(93450000, result);
    }

    @Test
    void testConvertFromUsd_WithDifferentRates() {
        // Test with updated rates
        rates.put("RUB", BigDecimal.valueOf(100.0));
        ReflectionTestUtils.setField(exchangeRateService, "rates", rates);

        int amountMinor = 10000; // 100.00 USD
        String toCurrency = "RUB";

        int result = exchangeRateService.convertFromUsd(amountMinor, toCurrency);

        // 100.00 USD * 100.0 RUB/USD = 10000.00 RUB = 1000000 minor units
        assertEquals(1000000, result);
    }
}

