package app.bot.handler.callback.payment;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.SendInvoiceResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.handler.callback.CallbackHandler;
import app.bot.state.UserStateService;
import app.module.converter.ExchangeRateServiceImpl;
import app.module.node.texts.BotTextService;
import app.module.payment.PaymentService;
import app.module.payment.props.PaymentOption;
import app.module.payment.props.PaymentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SendInvoicePaymentCallbackHandler implements CallbackHandler {

  private final UserStateService userStateService;
  private final PaymentProperties paymentProperties;
  private final PaymentService paymentService;
  private final ExchangeRateServiceImpl rateService;

  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals("UZS") ||
           callbackData.equals("USD") ||
           callbackData.equals("EUR") ||
           callbackData.equals("RUB");
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    String currency = query.getData();
    Long chatId = query.getMessage().getChatId();

    try {
      PaymentOption option = paymentProperties.resolve(currency);
      String providerToken = option.providerToken();
      int price = option.price();
      int amount;

      if (!currency.equals("USD")) {
        amount = rateService.convertFromUsd(price, currency);
      } else amount = price;

      String payload = "program_access_" + chatId;

      paymentService.createPayment(chatId, payload, amount, currency);

      SendInvoice invoice = SendInvoice.builder()
          .chatId(chatId.toString())
          .title("Доступ к программе")
          .description("Полный доступ к программе вибраций и чакр")
          .payload(payload)
          .providerToken(providerToken)
          .currency(currency)
          .prices(List.of(
              new LabeledPrice("Доступ", amount) // в копейках
          ))
          .startParameter("start")
          .build();

      return new SendInvoiceResponse(invoice);
    } catch (IllegalStateException e) {
      return new TextResponse(chatId, "Оплата в данной валюте временно недоступна", null);
    }
  }

}
