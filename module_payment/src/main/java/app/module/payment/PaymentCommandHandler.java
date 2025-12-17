package app.module.payment;

import app.core.payment.AccessService;
import app.core.payment.PaidPaymentInfo;
import app.core.payment.PaymentCommand;
import app.core.payment.PaymentResult;
import app.module.payment.dao.Payment;
import app.module.payment.dao.PaymentStatus;
import app.module.payment.repo.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentCommandHandler implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final AccessService accessService;

  @Override
  public void createPayment(Long chatId, String payload, int amount, String currency) {

    if (paymentRepository.existsByPayload(payload)) {
      return; // защита от повторного invoice
    }

    Payment p = new Payment();
    p.setChatId(chatId);
    p.setPayload(payload);
    p.setAmount(amount);
    p.setCurrency(currency);
    p.setStatus(PaymentStatus.CREATED);

    paymentRepository.save(p);
  }

  @Override
  public PaymentResult handlePayment(PaymentCommand command) {

    if (command.providerChargeId() != null
        && paymentRepository.existsByProviderPaymentId(command.providerChargeId())) {
      return new PaymentResult(true, "Payment already processed");
    }

    Payment entity = paymentRepository
        .findByPayload(command.payload())
        .orElseThrow(() ->
            new IllegalStateException(
                "Payment not found for payload=" + command.payload()
            )
        );

    if (entity.getStatus() == PaymentStatus.PAID) {
      return new PaymentResult(true, "Payment already processed");
    }

    entity.setStatus(PaymentStatus.PAID);
    entity.setPaidAt(LocalDateTime.now());
    entity.setProviderPaymentId(command.providerChargeId());

    paymentRepository.save(entity);

    accessService.grantAccess(
        new PaidPaymentInfo(
            entity.getId(),
            entity.getChatId(),
            entity.getPaidAt()
        )
    );


    return new PaymentResult(true, "Access granted");
  }
}
