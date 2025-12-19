package app.module.payment;

import app.core.payment.PaidPaymentInfo;
import app.core.payment.PaymentQueryService;
import app.module.payment.dao.PaymentStatus;
import app.module.payment.repo.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentQueryServiceImpl implements PaymentQueryService {

  private final PaymentRepository paymentRepository;

  @Override
  public Optional<PaidPaymentInfo> getPaidPayment(Long paymentId) {
    return paymentRepository.findById(paymentId)
        .filter(p -> p.getStatus() == PaymentStatus.PAID && p.getPaidAt() != null)
        .map(p -> new PaidPaymentInfo(
            p.getId(),
            p.getChatId(),
            p.getPaidAt()
        ));
  }
}
