package app.module.payment;

import app.core.payment.PaymentCommand;
import app.core.payment.PaymentResult;


public interface PaymentService {
  void createPayment(Long chatId, String payload, int amount, String currency);
  public PaymentResult handlePayment(PaymentCommand command);
}

