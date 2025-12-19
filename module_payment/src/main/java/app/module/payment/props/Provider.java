package app.module.payment.props;

import java.util.Set;

public record Provider(
    String token,
    Set<String> currencies,
    int price
) {
  public Provider {
    if (currencies == null) {
      currencies = Set.of(); // ← защита от NPE
    }
  }
}
