package app.bot.bot;

import app.bot.bot.responce.BotResponseProcessor;
import app.bot.bot.responce.TextResponse;
import app.bot.config.BotProperties;
import app.bot.dispatcher.CallbackDispatcher;
import app.bot.dispatcher.CommandDispatcher;
import app.bot.dispatcher.MessageDispatcher;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.sender.TelegramSender;
import app.bot.state.UserStateService;
import app.core.payment.PaymentCommand;
import app.core.program.DailyUpdateResult;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.payment.PaymentService;
import app.module.program.ProgramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@Slf4j
public class DemoBot extends BaseTelegramBot {

  private final PaymentService paymentService;
  private final UserStateService userStateService;
  private final ProgramService programService;
  private final BotTextService text;


  public DemoBot(
      BotProperties botProperties, CallbackDispatcher callbackDispatcher, CommandDispatcher commandDispatcher,
      MessageDispatcher messageDispatcher, PaymentService paymentService, UserStateService userStateService,
      TelegramSender telegramSender, ProgramService programService, BotTextService text, BotResponseProcessor botResponseProcessor) {
    super(botProperties, callbackDispatcher, commandDispatcher, messageDispatcher, botResponseProcessor, telegramSender);
    this.paymentService = paymentService;
    this.userStateService = userStateService;
    this.programService = programService;
    this.text = text;
  }

  // ================= PAYMENTS =================

  @Override
  protected void handlePreCheckout(Update update) {
    var query = update.getPreCheckoutQuery();

    AnswerPreCheckoutQuery answer = AnswerPreCheckoutQuery.builder()
        .preCheckoutQueryId(query.getId())
        .ok(true)
        .build();

    executeSafely(answer);
  }

  @Override
  protected void handleSuccessfulPayment(Update update) {
    var message = update.getMessage();
    var payment = message.getSuccessfulPayment();

    PaymentCommand command = new PaymentCommand(
        message.getFrom().getId(),
        message.getChatId(),
        payment.getInvoicePayload(),
        payment.getTotalAmount(),
        payment.getCurrency(),
        payment.getTelegramPaymentChargeId()
    );

    paymentService.handlePayment(command);
  }

  // ================= OPTIONAL =================

  @Override
  protected void handleUnknown(Update update) {
    log.warn("Unhandled update: {}", update);
  }

  //todo: back to default @Scheduled(cron = "00 00 08 * * *")
  @Scheduled(cron = "*/30 * * * * *")
  public void scheduledDailyUpdate() {
    log.info("DB daily update");

    List<DailyUpdateResult> updates = programService.dailyUpdate();
    log.info("updates = {}", updates);

    for (DailyUpdateResult upd : updates) {

      TextResponse response = new TextResponse(
          upd.chatId(),
          text.format(TextMarker.SCHEDULER_MESSAGE),
          KeyboardFactory.from(
              List.of(
                  new KeyboardOption("Ура!", TextMarker.PROGRAM)
              )
          )
      );

      telegramSender.send(response);
    }
  }

}
