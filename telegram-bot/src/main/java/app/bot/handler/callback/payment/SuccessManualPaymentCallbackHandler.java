package app.bot.handler.callback.payment;

import app.bot.bot.responce.*;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.payment.PaymentCommand;
import app.core.program.CompositeProgramMessage;
import app.core.program.ProgramMessage;
import app.module.chat.service.ChatHistoryService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.payment.PaymentService;
import app.module.program.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SuccessManualPaymentCallbackHandler implements CallbackHandler {

  private final ProgramService programService;
  private final BotTextService textService;
  private final UserStateService userStateService;
  private final ChatHistoryService chatHistoryService;
  private final AnalyticsFacade analyticsFacade;
  private final PaymentService paymentService;



  @Override
  public boolean supports(String callbackData) {
    return callbackData.startsWith("MANUAL_SUCCESS_PAYMENT");
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();

    String callbackData = query.getData();
    String[] parts = callbackData.split(" ");

    if (parts.length != 2) {
      throw new IllegalArgumentException("Invalid callback data: " + callbackData);
    }

    Long userChatId = Long.parseLong(parts[1]);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    userStateService.setState(userChatId, UserState.COURSE);
    analyticsFacade.trackPaymentSuccess(userChatId);
    analyticsFacade.trackBlockView(userChatId, "SUCCESS_PAYMENT", Map.of("source", "payment_flow"));
    analyticsFacade.trackCtaShown(userChatId, TextMarker.PROGRAM);

    String payload = "program_access_" + chatId;

    PaymentCommand command = new PaymentCommand(
        query.getFrom().getId(),
        userChatId,
        payload,
        100,
        "USD",
        "MANUAL"
    );


    paymentService.handlePayment(command);

    TextResponse textForUser = new TextResponse(userChatId, "✅ Оплата прошла успешно! Добро пожаловать в программу",
        KeyboardFactory.from(List.of(new KeyboardOption("Начать", TextMarker.PROGRAM))));

    TextResponse textForAdmin = new TextResponse(chatId, "✅ Пользователь " + userChatId + " успешно добавлен в курс", null);

    compositeResponse.responses().add(textForUser);
    compositeResponse.responses().add(textForAdmin);

    return compositeResponse;
  }
}