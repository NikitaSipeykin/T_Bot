package app.bot.handler.callback.payment;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.CompositeResponse;
import app.bot.bot.responce.SendInvoiceResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.module.converter.ExchangeRateServiceImpl;
import app.module.node.texts.TextMarker;
import app.module.payment.PaymentService;
import app.module.payment.props.PaymentOption;
import app.module.payment.props.PaymentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SendInvoicePaymentCallbackHandler implements CallbackHandler {

  private final UserStateService userStateService;
  private final PaymentProperties paymentProperties;
  private final PaymentService paymentService;
  private final ExchangeRateServiceImpl rateService;
  private final AnalyticsFacade analytics;

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
    userStateService.setState(chatId, UserState.PAYMENT);
    analytics.trackButtonClick(query, "CURRENCY_" + currency);

    try {
      PaymentOption option = paymentProperties.resolve(currency);
      String providerToken = option.providerToken();
      int price = option.price();
      int amount;

      if (!currency.equals("USD")) {
        amount = rateService.convertFromUsd(price, currency);
      } else amount = price;

      String payload = "program_access_" + chatId;
      analytics.trackPaymentStart(chatId, payload, amount, currency);

      paymentService.createPayment(chatId, payload, amount, currency);

      CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());;

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

      SendInvoiceResponse invoiceResponse = new SendInvoiceResponse(invoice);

      TextResponse response = new TextResponse(chatId, "Выбрать другую валюту!",
          KeyboardFactory.from(List.of(new KeyboardOption("Назад", TextMarker.PAYMENT))));

      compositeResponse.responses().add(invoiceResponse);
      compositeResponse.responses().add(response);
      analytics.trackInvoiceShown(chatId, payload, currency);

      return compositeResponse;
    } catch (IllegalStateException e) {
      analytics.trackPaymentUnavailable(chatId, currency, e.getMessage());
      return new TextResponse(chatId, "Оплата в данной валюте временно недоступна", null);
    }
  }

}
