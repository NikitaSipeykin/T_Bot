package app.module.payment.repo;

import app.module.payment.dao.Payment;
import app.module.payment.dao.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  Optional<Payment> findByPayload(String payload);

  boolean existsByProviderPaymentId(String providerPaymentId);

  Optional<Payment> findByIdAndStatus(Long id, PaymentStatus status);

  Optional<Payment> findFirstByChatIdAndStatusOrderByPaidAtDesc(
      Long chatId,
      PaymentStatus status
  );

  boolean existsByPayload(String payload);
}
