package app.bot.handler.callback.payment;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.CompositeResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.config.BotProperties;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.module.node.texts.TextMarker;
import app.module.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ManualPaymentCallbackHandler implements CallbackHandler {

  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;
  private final BotProperties botProperties;
  private final PaymentService paymentService;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.SEND_PAYMENT_REQUEST);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    User user = query.getFrom();
    String userName = user.getUserName() != null
        ? "@" + user.getUserName()
        : user.getFirstName();

    userStateService.setState(chatId, UserState.MANUAL_PAYMENT);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    String adminText = """
        Пользователь отправил заявку на доступ к курсу.
              
        Chat ID: %d
        Username: %s
      
        Проверьте пожалуйста оплату.
        """.formatted(
        chatId,
        userName
    );
    String payload = "program_access_" + chatId;

    paymentService.createPayment(chatId, payload, 100, "USD");

    Long adminId = botProperties.getAdminId();

    TextResponse textForAdmin = new TextResponse(adminId, adminText, KeyboardFactory.from(Collections.singletonList(new
        KeyboardOption("Предоставить доступ.", "MANUAL_SUCCESS_PAYMENT " + chatId))));

    TextResponse textForUser = new TextResponse(chatId, "Дождитесь ответа от админестратора!", null);

    compositeResponse.responses().add(textForAdmin);
    compositeResponse.responses().add(textForUser);

    return compositeResponse;
  }
}