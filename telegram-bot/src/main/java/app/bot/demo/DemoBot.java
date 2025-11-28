package app.bot.demo;

import app.bot.BaseTelegramBot;
import app.bot.TelegramMessageSender;
import app.bot.email.EmailService;
import app.text.node.texts.BotTextService;
import app.core.BroadcastService;
import app.core.SubscriberService;
import app.video.node.VideoNoteService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DemoBot extends BaseTelegramBot {
  private final SubscriberService subscriberService;
  private final BroadcastService broadcastService;
  private final EmailService emailService;
  private final BotTextService text;

  private int currentState = 0;


  public DemoBot(SubscriberService subscriberService, EmailService emailService, BotTextService text,
                 VideoNoteService videoNoteService) {
    this.emailService = emailService;
    this.videoNoteService = videoNoteService;
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

    if (messageText.equals(Commands.START)) {
      subscriberService.subscribe(chatId, username, firstName);
      startCommand(chatId, firstName);
    } else if (messageText.equals(Commands.CIRCLE)) {
      sendVideoNote(chatId, Commands.KEY_START);
    } else if (messageText.equals(Commands.UNSUBSCRIBE)) {
      unsubscribeCommand(chatId);
    } else if (messageText.startsWith(Commands.BROADCAST)) {
      broadcastCommand(chatId, messageText, userId);
    } else stateProcessing(chatId, messageText);
  }

  @Override
  public void callbackProcessing(Update update) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    String data = update.getCallbackQuery().getData();

    switch (data) {
      case Commands.FIRST -> {
        sendMessage(chatId, text.get("FIRST_STEP"), null);
        currentState = Commands.MAIL_REQUEST_STATE;
      }
      case Commands.SECOND -> {
        sendMessage(chatId, text.get("SECOND_STEP"), null);
        currentState = Commands.DEFAULT_STATE;
      }
    }
  }

  private void stateProcessing(Long chatId, String messageText) {
    switch (currentState) {
      case Commands.DEFAULT_STATE -> sendMessage(chatId, text.get("ERROR"), null);
      case Commands.MAIL_REQUEST_STATE -> emailRequestState(chatId, messageText);
      case Commands.WAIT_MAIL_STATE -> waitMailState(chatId, messageText);
      case Commands.PRISE_STATE -> priseState(chatId);
      default -> sendMessage(chatId, "Проверьте состояние.", null);
    }
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
    // TODO: отправить подарок
  }

  // COMMANDS ===========================================
  private void startCommand(Long chatId, String firstName) {
    sendMessage(chatId, text.format("START", firstName != null ? firstName : "друг"),
        keyboard(button("Приступим?", Commands.FIRST)));
  }

  private void broadcastCommand(Long chatId, String messageText, Long userId) {
    // Для безопасности — разрешим только админскую команду
    Long adminId = props.getAdminId();
    if (adminId != null && adminId.equals(userId)) {
      String body = messageText.substring("/broadcast ".length());
      broadcastService.broadcast(body);
    } else {
      sendMessage(chatId, text.get("BROADCAST_FAIL"), null);
    }
  }

  private void unsubscribeCommand(Long chatId) {
    subscriberService.unsubscribe(chatId);
    sendMessage(chatId, text.get("UNSUBSCRIBE"), null);
  }
}

