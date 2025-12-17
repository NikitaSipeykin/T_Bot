package app.bot.mane;

import app.bot.TelegramMessageSender;
import app.bot.email.EmailService;
import app.core.broadcast.BroadcastService;
import app.core.broadcast.SubscriberService;
import app.core.payment.PaymentCommand;
import app.core.program.DailyUpdateResult;
import app.core.program.ProgramMessage;
import app.core.test.FinalMessage;
import app.core.test.OutgoingMessage;
import app.module.converter.ExchangeRateServiceImpl;
import app.module.payment.PaymentService;
import app.module.payment.props.PaymentOption;
import app.module.payment.props.PaymentProperties;
import app.module.program.ProgramService;
import app.module.test.TestService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.node.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class DemoBot extends BaseTelegramBot {
  private final SubscriberService subscriberService;
  private final BroadcastService broadcastService;
  private final EmailService emailService;
  private final TestService testService;
  private final BotTextService text;
  private final PaymentService paymentService;
  private final PaymentProperties paymentProperties;
  private final ExchangeRateServiceImpl rateService;

  private int currentState = Commands.DEFAULT_STATE;

  public DemoBot(
      SubscriberService subscriberService, EmailService emailService, BotTextService text,
      PaymentService paymentService, NoteService noteService, TestService testService, ProgramService programService,
      PaymentProperties paymentProperties, ExchangeRateServiceImpl rateService) {
    this.emailService = emailService;
    this.paymentService = paymentService;
    this.testService = testService;
    this.paymentProperties = paymentProperties;
    this.rateService = rateService;
    this.programService = programService;
    this.noteService = noteService;
    TelegramMessageSender messageSender = new TelegramMessageSender(this);
    this.subscriberService = subscriberService;
    this.broadcastService = new BroadcastService(subscriberService, messageSender);
    this.text = text;
  }

  // PROCESSING =========================================
  @Override
  public void messageProcessing(Update update) {
    String messageText = update.getMessage().getText();
    Long userId = update.getMessage().getFrom().getId();
    Long chatId = update.getMessage().getChatId();
    String username = update.getMessage().getFrom().getUserName();
    String firstName = update.getMessage().getFrom().getFirstName();

    if (programService.checkUserAccessProgram(chatId)) {
      program(chatId);
      return;
    }

    log.info("currentState = " + currentState + "\n");

    if (messageText.equals(Commands.START)) {
      subscriberService.subscribe(chatId, username, firstName);
      startCommand(chatId, firstName);
    } else if (messageText.equals(Commands.CIRCLE)) {
      log.info("Пользователь попытался вызвать команду CIRCLE. chatId={}", chatId);
    } else if (messageText.equals(Commands.UNSUBSCRIBE)) {
      unsubscribeCommand(chatId);
    } else if (messageText.equals(Commands.DEBUG)) {
      //todo: debug
      programBegin(chatId);
    } else if (messageText.startsWith(Commands.BROADCAST)) {
      broadcastCommand(chatId, messageText, userId);
    } else stateProcessing(chatId, messageText);
  }

  @Override
  public void callbackProcessing(Update update) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    String data = update.getCallbackQuery().getData();

    log.info("currentState = " + currentState + "\n");

    if (data.startsWith("TEST_Q_")) {
      testProcessing(chatId, data);
      return;
    }

    if (programService.checkUserAccessProgram(chatId)) {
      program(chatId);
      return;
    }

    if (currentState == Commands.PAYMENT){
      switch (data){
        case "UZS" -> {sendInvoice(chatId, "UZS");}
        case "USD" -> {sendInvoice(chatId, "USD");}
        case "EUR" -> {sendInvoice(chatId, "EUR");}
        case "RUB" -> {sendInvoice(chatId, "RUB");}
      }
    }

    switch (data) {
      case TextMarker.PRESENT_GIDE -> {presentGide(chatId);}
      case TextMarker.CHAKRA_INTRO -> {startTest(chatId);}
      case TextMarker.TEST_END -> {sendMessage(chatId, text.format(TextMarker.TEST_END),
            keyboard(button("Хорошо", TextMarker.PRESENT)));}

      case TextMarker.PRESENT -> {priseState(chatId);}
      case TextMarker.VIBRATIONS_AND_CHAKRAS -> {paymentState(chatId);}
      case TextMarker.PAYMENT -> {payment(chatId);}
      case TextMarker.INFO_PROGRAM -> {sendMessage(chatId, text.format(TextMarker.INFO_PROGRAM),
          keyboard(button("Записаться на курс", TextMarker.PAYMENT)));}
      //todo: debug
      case TextMarker.DEBUG -> {debugDummy(chatId);}
      case TextMarker.PROGRAM -> programBegin(chatId);
    }
  }

  private void stateProcessing(Long chatId, String messageText) {
    switch (currentState) {
      case Commands.DEFAULT_STATE -> sendMessage(chatId, text.get(TextMarker.ERROR), null);
      case Commands.MAIL_REQUEST_STATE -> emailRequestState(chatId, messageText);
      case Commands.WAIT_MAIL_STATE -> waitMailState(chatId, messageText);
      case Commands.PRISE_STATE -> priseState(chatId);
      case Commands.NEED_PAYMENT -> needPayment(chatId);
      case Commands.PROGRAM_STATE -> program(chatId);
      default -> log.error("Проверьте работу состояний. или последовательность действий");
    }
  }

  // PROGRAM ==============================================
  private void programBegin(Long chatId) {
    log.info("programBegin");
    Object response = programService.startProgram(chatId);
    currentState = Commands.PROGRAM_STATE;

    if (response instanceof ProgramMessage m) {
      sendMessage(chatId, text.format(m.text()), toKeyboard(m.options()));
    }
  }

  private void program(Long chatId) {
    if (programService.checkUserAccessProgram(chatId)) {
      Object response = programService.nextMessage(chatId);
      currentState = Commands.PROGRAM_STATE;

      if (response instanceof ProgramMessage m) {
        if (m.options().isEmpty()){
          sendMessage(chatId, text.format(m.text()), null);

          if (m.text().endsWith(TextMarker.AUDIO_MARKER)){
            sendAudioNote(chatId, m.text());
          }

          if (m.shouldBeNext()){program(chatId);}
          return;
        }
        sendMessage(chatId, text.format(m.text()), toKeyboard(m.options()));
        return;
      }
      log.error("nothing");
      return;
    }
    needPayment(chatId);
  }

  // PAYMENT ==============================================
  private void paymentState(Long chatId) {
    currentState = Commands.NEED_PAYMENT;
    sendMessage(chatId, text.format(TextMarker.VIBRATIONS_AND_CHAKRAS), keyboard(
        //todo: debug
        button("Да, записаться!", TextMarker.PAYMENT),
        button("Расскажи подробнее", TextMarker.INFO_PROGRAM)));
  }

  private void needPayment(Long chatId) {
    sendMessage(chatId, text.format(TextMarker.NEED_PAYMENT), keyboard(
        button("Да, записаться!", TextMarker.PAYMENT),
        button("Расскажи подробнее", TextMarker.INFO_PROGRAM)));
  }

  private void payment(Long chatId) {
    currentState = Commands.PAYMENT;
    sendMessage(chatId, "Выберите валюту для оплаты", keyboard(
        button("UZS", "UZS"),
        button("USD", "USD"),
        button("EUR", "EUR"),
        button("RUB", "RUB")));
  }

  private void sendInvoice(Long chatId, String currency) {
    try {
      PaymentOption option = paymentProperties.resolve(currency);
      String providerToken = option.providerToken();
      int price = option.price();
      int amount;
      if (!currency.equals("USD")){
        amount = rateService.convertFromUsd(price, currency);
      }else amount = price;

      String payload = "program_access_" + chatId;

      paymentService.createPayment(
          chatId,
          payload,
          amount,
          currency
      );

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

      try {
        execute(invoice);
      } catch (TelegramApiException e) {
        log.error("Ошибка отправки invoice", e);
      }
    }catch (IllegalStateException e) {
      sendMessage(chatId,
          "Оплата в данной валюте временно недоступна", null);
    }
  }

  @Override
  public void handlePreCheckout(Update update) {
    PreCheckoutQuery query = update.getPreCheckoutQuery();

    AnswerPreCheckoutQuery answer = AnswerPreCheckoutQuery.builder()
        .preCheckoutQueryId(query.getId())
        .ok(true) // ОБЯЗАТЕЛЬНО true
        .build();

    try {
      execute(answer);
    } catch (TelegramApiException e) {
      log.error("Ошибка PreCheckoutQuery", e);
    }
  }

  @Override
  public void successfulPaymentProcessing(Update update) {
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

    sendMessage(
        message.getChatId(),
        "✅ Оплата прошла успешно! Добро пожаловать в программу",
        keyboard(button("Начать", TextMarker.PROGRAM))
    );
  }


  // PRESENT ==============================================
  private void presentGide(Long chatId) {
    sendMessage(chatId, text.get(TextMarker.PRESENT_GIDE), null);
    sendDocument(chatId, Commands.DOC_GIDE);
    try {
      Thread.sleep(30000);
      sendMessage(chatId, text.get(TextMarker.READY_TO_GIDE),
          keyboard(button("Да, проверим чакры!", TextMarker.CHAKRA_INTRO)));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  // TEST =================================================
  private void startTest(Long chatId) {
    if (!subscriberService.isFinishedTesting(chatId)) {
      Object response = testService.startTest(chatId);
      currentState = Commands.TEST_STATE;

      if (response instanceof OutgoingMessage m) {
        sendMessage(chatId, m.text(), toKeyboard(m.options()));
      }
      return;
    }
    sendMessage(chatId, text.format(TextMarker.TEST_END_ALREADY),
        keyboard(button("Хочу больше!", TextMarker.VIBRATIONS_AND_CHAKRAS)));
  }

  private void testProcessing(Long chatId, String data) {
    Object response = testService.processAnswer(chatId, data);

    if (response instanceof OutgoingMessage m) {
      if (m.isNextTopic()) {
        sendMessage(chatId, text.format(TextMarker.GOT_YOU), null);
      }
      sendMessage(chatId, m.text(), toKeyboard(m.options()));
    } else if (response instanceof FinalMessage f) {
      sendMessage(chatId, f.text(), null);
      testService.saveResultTopics(chatId, f.recommendedTopicNames());

      if (Objects.equals(f.text(), text.format(TextMarker.ALL_ZERO))) {
        sendMessage(chatId, text.format(TextMarker.ALL_ZERO_RESULT), keyboard(
            button("Хорошо!", TextMarker.PAYMENT),
            button("Попробовать еще раз!", TextMarker.CHAKRA_INTRO)));
        return;
      }

      subscriberService.setFinishedTest(chatId);
      sendMessage(chatId, text.format(TextMarker.RESULT),
          keyboard(button("Хочу решения!", TextMarker.TEST_END)));
    }
  }

  private void priseState(Long chatId) {
    List<String> topics = testService.getResultTopics(chatId);
    log.info("topics = " + topics);

    for (String topic : topics) {
      log.info("topic = " + topic);
      sendMessage(chatId, text.format(topic + "_PRESENT"), null);
    }
    sendMessage(chatId, text.format(TextMarker.PRESENT_END),
        keyboard(button("Хочу больше!", TextMarker.VIBRATIONS_AND_CHAKRAS)));
  }

  // EMAIL ==============================================
  private void emailRequestState(Long chatId, String messageText) {
    if (!emailService.isValidEmail(messageText)) {
      sendMessage(chatId, "Кажется, это не почта. Попробуй снова:", null);
      return;
    }

    subscriberService.setEmail(chatId, messageText);
    String code = emailService.generateCode();
    subscriberService.setCode(chatId, code);
    currentState = Commands.WAIT_MAIL_STATE;
    emailService.sendVerificationCode(messageText, code);
    sendMessage(chatId, "Я отправил код на почту. Введите его:", null);
  }

  private void waitMailState(Long chatId, String messageText) {
    String code = subscriberService.getCode(chatId);
    if (!messageText.equals(code)) {
      sendMessage(chatId, "Неверный код! Попробуй снова.", null);
      return;
    }

    subscriberService.setVerified(chatId);
    currentState = Commands.PRISE_STATE;
    sendMessage(chatId, "Отлично! Вот твой подарок 🎁", null);
  }

  // COMMANDS ===========================================
  private void startCommand(Long chatId, String firstName) {
    currentState = Commands.DEFAULT_STATE;
    sendVideoNote(chatId, Commands.VIDEO_START);
    sendMessage(chatId, text.format(TextMarker.START, firstName != null ? firstName : "друг"),
        keyboard(button("Да!", TextMarker.PRESENT_GIDE)));
  }

  private void broadcastCommand(Long chatId, String messageText, Long userId) {
    Long adminId = props.getAdminId();
    if (adminId != null && adminId.equals(userId)) {
      String body = messageText.substring("/broadcast ".length());
      broadcastService.broadcast(body);
    } else {
      sendMessage(chatId, text.get(TextMarker.BROADCAST_FAIL), null);

    }
  }

  private void unsubscribeCommand(Long chatId) {
    subscriberService.unsubscribe(chatId);
    sendMessage(chatId, text.get(TextMarker.UNSUBSCRIBE), null);
  }

  //@Scheduled(cron = "00 00 08 * * *") 11 msc
  @Override
  @Scheduled(cron = "00 00 08 * * *")
  public void scheduledDailyUpdate() {
    log.info("\n" +
             "DB daily update");

    List<DailyUpdateResult> updates = programService.dailyUpdate();

    log.info("updates = " + updates);
    for (DailyUpdateResult upd : updates) {
      sendMessage(upd.chatId(), text.format(TextMarker.SCHEDULER_MESSAGE),
          keyboard(button("Ура!", TextMarker.PROGRAM)));
    }
  }



  /* Я мудак - если я это здесь оставлю
⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠏⠀⢀⠀⡰⢃⣿⡿⠁⡼⠃⣰⡟⢁⣼⠟⣰⣿⣾⠿⣿⢾⣿⣿⠟⢷⣶⢶⣶⡄⢻⣿⣿⣷⣷⡻⣯⠙
⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠏⠀⠐⠁⣼⠃⡾⡟⢀⠞⠁⣼⠋⠀⣾⠟⢠⣿⣻⣞⡿⣷⣿⠟⠎⠀⣸⣞⣻⣿⣿⡀⢹⣷⣻⢾⣷⠌⠂
⣻⣾⣿⣿⣿⣿⣿⣿⣿⣿⡟⠀⠐⠀⣸⠃⢸⣿⠀⡬⠀⣘⠃⠄⣸⡟⠀⣾⣿⣳⣏⣿⡿⠋⠀⢠⠆⣿⣯⢳⣻⣇⢳⠀⣿⣿⣻⣻⡆⠀
⣻⣟⣿⣿⣿⣿⣿⣿⣿⣿⣿⠁⠠⠠⡗⠀⣿⠃⠰⠁⢠⠏⠠⠀⡜⠀⢠⣿⣷⡿⡿⠻⠁⠀⢠⠸⠀⠿⢯⠷⢿⣯⠀⡆⢸⣿⣾⣽⡇⠀
⣿⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⠄⠆⢸⠐⠀⣿⠀⠀⠀⠀⠀⠃⠘⢠⠂⢼⣿⡷⣷⠒⠀⢀⣼⣶⠀⠀⢲⡶⢰⣦⣄⠀⠃⢈⠓⠉⡈⠀⢠
⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡂⠇⢨⠐⠀⡧⠀⡃⠀⠀⠀⠀⠀⡟⠀⣿⣿⠟⢁⠄⠠⠞⠻⢿⠂⢰⠘⡇⢸⣽⣎⡅⠀⠨⢄⠂⠁⠀⣧
⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡃⢀⠈⠆⠀⢡⢀⠇⠀⠀⠀⠀⢰⡇⢀⣿⠏⢀⡜⢀⡄⠀⠀⠈⠀⠚⠀⠁⡞⣿⣯⠁⠀⢢⠘⠀⠀⡆⢿
⣿⣿⣿⣿⡿⠟⠻⠿⠿⠿⠟⠇⠈⠀⠀⠀⡀⠸⣆⠠⢆⣀⢠⣿⣿⠀⠃⣰⡟⣴⣿⠀⠀⠀⠀⠀⠀⣀⠀⢸⣿⡗⠀⠀⢂⠠⢀⣾⡇⣺
⣿⣿⣿⡏⠀⢠⣀⣀⡐⢀⠂⠀⠄⠀⠐⠀⠳⠈⣿⣶⣤⣾⣿⣿⡗⢀⣾⣿⣿⣿⣿⠀⣠⠀⡀⠀⣠⠏⢠⣿⣟⠃⠀⡐⠂⣠⠟⣿⡇⢸
⣿⣿⡟⠀⠄⣼⣿⣿⣿⣿⣿⣿⣶⣶⣦⣤⣄⣁⡘⠛⠛⠛⠛⠿⠷⣿⣿⣿⣿⣿⣿⣦⣀⣉⣡⣾⠏⢀⣾⡿⠂⡄⠀⣠⣾⣿⡆⢻⣯⠀
⢻⣿⠁⠘⢠⣿⣿⣿⠋⠉⠉⣿⣿⠏⣿⣿⣿⣿⣿⣿⣿⣿⣷⣶⣦⣤⣤⣈⣉⣉⣋⠉⠛⠙⠏⠁⢠⣾⡟⢁⡔⠀⢠⣿⣏⣷⡳⠘⣿⡀
⢾⣿⠀⠂⣼⣿⣿⡇⢸⣿⣷⣿⡿⠠⠻⢀⣶⢸⣿⣿⠟⠻⠿⢿⣿⣿⣿⣿⣿⣾⣿⣿⣿⣿⣶⣶⣦⣤⠀⢀⠠⠀⠸⣿⣏⡷⣿⡀⢿⡷
⡉⠋⠀⢠⣿⣿⣿⣇⣈⣀⣿⣿⠁⣼⡇⠸⠟⢸⣿⡿⢀⣶⣦⡀⢸⣿⣿⠟⠛⠻⣿⣿⣿⣿⣿⣿⣿⡿⠀⠨⢜⡀⠀⣿⣯⣟⣿⡇⠈⢿
⠀⡀⠈⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣶⣾⣿⣿⠁⠸⠿⠿⢁⣾⣿⡏⢠⣾⡶⢀⣿⣿⣿⣿⣿⣿⠇⠀⠉⡆⠄⠀⢻⣿⢾⣽⣷⠀⠈
⠀⣶⡚⢽⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣶⣶⣿⣿⣿⡇⢿⡿⠃⢸⣿⣿⣿⣿⣿⡿⠀⠨⠄⠘⡥⠀⠘⣿⣯⣟⣻⡄⠀
⠀⢿⢇⣾⣿⣿⠇⢘⣿⣿⡿⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣶⣶⡆⢿⣿⣿⣿⣯⢰⣆⠀⠘⡰⠈⡔⢣⠀⢹⣿⣼⣳⣧⠠
⠀⢀⣾⣿⣿⣟⠀⣼⣿⡟⠀⣿⠏⣤⣈⠛⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣇⠀⣶⣾⡄⠱⡀⢘⢂⠆⠀⢻⣶⣻⣷⡈
⠀⣸⣿⣿⣿⠃⠴⢿⡿⠐⣸⡿⢠⣿⣿⣿⣶⣾⣿⡿⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⠀⢉⣤⣦⠀⢡⠀⢎⠘⠤⠀⢻⣿⣷⣷
⡄⣿⣿⣿⡟⣰⣦⣤⢀⢰⣿⠀⠘⠻⣿⣿⣿⣿⠟⡀⣼⣿⢃⣾⣿⣿⠿⠿⣿⣿⣿⣿⣿⣿⣿⡆⢸⣿⣻⠀⠌⡄⠘⢀⢆⢂⠈⢻⣿⣟
⢠⣿⣿⡿⣀⣿⣿⠃⣰⣿⠃⣼⣿⣿⣿⣿⡟⢡⣾⢀⣿⠃⣼⣿⣿⣿⣾⣧⠈⣿⡇⢀⡄⢹⣿⠀⠐⣧⣿⠀⠘⡄⢈⠀⢎⡰⢀⠀⠻⣿
⣾⣿⣿⣿⣿⣿⣿⣶⣿⣅⣈⠛⢿⣿⣿⠋⢰⣿⠇⢸⡛⣨⡉⢿⣿⣿⣤⠁⣾⣿⣧⡌⠁⣾⣿⢀⡀⠻⠾⠀⠰⠐⠀⠀⣎⠰⠀⡀⠀⠙
⠉⠉⡙⢛⠛⠻⠿⠿⣿⣿⣿⣿⣿⣿⣿⣷⣿⣿⣤⣿⣇⠻⠏⣸⡿⣿⣿⠇⣿⣿⠏⣰⢢⣿⡇⠈⠓⣀⢄⠲⣐⠆⠀⡸⢀⢣⠀⢸⠀⠀
⣴⣦⣤⣤⡍⣁⡐⠂⠠⠠⠉⢉⡙⠛⠛⠻⠿⠿⠿⣿⣿⣷⣿⣿⣷⣤⣴⠾⢛⢁⣴⣿⢸⣿⠀⠲⢍⠲⣌⡱⢌⠂⢠⡑⢌⢢⠀⢸⡇⠀
⣿⣿⣿⡿⠿⡿⢶⠋⠑⠶⠶⠆⠠⠀⢐⠒⠀⠄⠐⢀⠠⡌⢉⡛⢛⠛⠿⠆⠥⣿⣿⣿⣸⡏⠠⠤⠤⡌⢆⠱⠉⠀⠆⡜⠤⣊⠄⣹⣇⠀
  * */

  //todo: debug
  private void debugDummy(Long chatId) {
    sendMessage(chatId, "Эта часть в разработке!",
        keyboard(button("Перейти к программе", TextMarker.PROGRAM)));
  }
}

