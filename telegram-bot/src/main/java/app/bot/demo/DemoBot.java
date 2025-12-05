package app.bot.demo;

import app.bot.BaseTelegramBot;
import app.bot.TelegramMessageSender;
import app.core.AnswerOption;
import app.bot.email.EmailService;
import app.core.*;
import app.module.test.TestService;
import app.text.node.texts.BotTextService;
import app.text.node.texts.TextMarker;
import app.video.node.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Objects;

@Component
public class DemoBot extends BaseTelegramBot {
  private static final Logger log = LoggerFactory.getLogger(DemoBot.class);

  private final SubscriberService subscriberService;
  private final BroadcastService broadcastService;
  private final EmailService emailService;
  private final TestService testService;
  private final BotTextService text;

  private int currentState;


  public DemoBot(
      SubscriberService subscriberService,
      EmailService emailService,
      BotTextService text,
      NoteService noteService,
      TestService testService
  ) {
    this.emailService = emailService;
    this.testService = testService;
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

//    if (chatId != 303885621){
      if (messageText.equals(Commands.START)) {
        subscriberService.subscribe(chatId, username, firstName);
        startCommand(chatId, firstName);
      } else if (messageText.equals(Commands.CIRCLE)) {
//      sendVideoNote(chatId, Commands.KEY_START);
        log.info("Пользователь попытался вызвать команду CIRCLE. chatId={}", chatId);
      } else if (messageText.equals(Commands.UNSUBSCRIBE)) {
        unsubscribeCommand(chatId);
      } else if (messageText.startsWith(Commands.BROADCAST)) {
        broadcastCommand(chatId, messageText, userId);
      } else stateProcessing(chatId, messageText);
//    }else {
//      sendMessage(chatId, text.get(TextMarker.ERROR), null);
//      log.info("Пользователь попытался взаимодействовать с ботом. chatId={}", chatId);
//    }
  }

  @Override
  public void callbackProcessing(Update update) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    String data = update.getCallbackQuery().getData();


    log.info("chatId = " + chatId + " data = " + data + " currentState = " + currentState);
    if (data.startsWith("TEST_Q_")) {
      testProcessing(chatId, data);
      return;
    }

    switch (data) {
      case TextMarker.PRESENT_GIDE -> {presentGide(chatId);}
      case TextMarker.CHAKRA_INTRO -> {startTest(chatId);}
      case TextMarker.PRESENT -> {priseState(chatId);}
    }
  }

  private void testProcessing(Long chatId, String data) {
    Object response = testService.processAnswer(chatId, data);
    log.info("response = " + response);
    if (response instanceof OutgoingMessage m) {
      if (m.isNextTopic()){
        sendMessage(chatId, text.format(TextMarker.GOT_YOU), null);
      }
      sendMessage(chatId, m.text(), toKeyboard(m.options()));
    } else if (response instanceof FinalMessage f) {
      sendMessage(chatId, f.text(), null);
      if (!Objects.equals(f.text(), text.format(TextMarker.ALL_ZERO)))
      subscriberService.setFinishedTest(chatId);
      sendMessage(chatId, text.format(TextMarker.RESULT),
          keyboard(button("Хочу решения!", TextMarker.PRESENT)));

    }
  }

  private List<List<InlineKeyboardButton>> toKeyboard(List<AnswerOption> opts) {
    return opts.stream()
        .map(opt -> List.of(
            InlineKeyboardButton.builder()
                .text(opt.getText())
                .callbackData(opt.getCallback())
                .build()
        ))
        .toList();
  }

  private void stateProcessing(Long chatId, String messageText) {
    switch (currentState) {
      case Commands.DEFAULT_STATE -> sendMessage(chatId, text.get(TextMarker.ERROR), null);
      case Commands.MAIL_REQUEST_STATE -> emailRequestState(chatId, messageText);
      case Commands.WAIT_MAIL_STATE -> waitMailState(chatId, messageText);
      case Commands.PRISE_STATE -> priseState(chatId);
      default -> log.error("Проверьте работу состояний. или последовательность действий");
    }
  }

  // EVENT ==============================================
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

  private void startTest(Long chatId) {
    if (!subscriberService.isFinishedTesting(chatId)){
      Object response = testService.startTest(chatId);
      currentState = Commands.TEST_STATE;

      log.info("response = " + response);
      if (response instanceof OutgoingMessage m) {
        sendMessage(chatId, m.text(), toKeyboard(m.options()));
      } else if (response instanceof FinalMessage f) {
        sendMessage(chatId, f.text(), null);
      }
      return;
    }
    sendMessage(chatId, text.format(TextMarker.PRESENT_END),
        keyboard(button("Хочу решения!", TextMarker.PRESENT)));
  }

  // STATE ==============================================
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

  private void priseState(Long chatId) {
    sendMessage(chatId, "priseState", null);
  }

  // COMMANDS ===========================================
  private void startCommand(Long chatId, String firstName) {
    sendMessage(chatId, text.format(TextMarker.START, firstName != null ? firstName : "друг"),
        keyboard(button("Да!", TextMarker.PRESENT_GIDE)));
    //todo: Add video-note
    sendVideoNote(chatId, Commands.VIDEO_START);
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
}

