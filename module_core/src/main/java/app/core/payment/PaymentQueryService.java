package app.core.payment;

import java.util.Optional;

public interface PaymentQueryService {

  Optional<PaidPaymentInfo> getPaidPayment(Long paymentId);

}
