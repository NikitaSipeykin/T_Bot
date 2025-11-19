package app.bot.demo;

import app.bot.BaseTelegramBot;
import app.bot.TelegramMessageSender;
import app.core.BroadcastService;
import app.core.SubscriberService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DemoBot extends BaseTelegramBot {
  private final SubscriberService subscriberService;
  private final BroadcastService broadcastService;
  private int currentState = 0;

  public DemoBot(SubscriberService subscriberService) {
    TelegramMessageSender messageSender = new TelegramMessageSender(this);
    this.subscriberService = subscriberService;
    this.broadcastService = new BroadcastService(subscriberService, messageSender);
  }

  // PROCESSING =========================================
  @Override
  public void messageProcessing(Update update) {
    String messageText = update.getMessage().getText();
    Long chatId = update.getMessage().getChatId();
    String username = update.getMessage().getFrom().getUserName();
    String firstName = update.getMessage().getFrom().getFirstName();

    if (messageText.equals(Commands.START)) {
      subscriberService.subscribe(chatId, username, firstName);
      startCommand(chatId, firstName);
    }
    else if (messageText.equals(Commands.UNSUBSCRIBE)) {
      unsubscribeCommand(chatId);
    }
    else if (messageText.startsWith(Commands.BROADCAST)) {
      broadcastCommand(messageText, chatId);
    }
    else stateProcessing(chatId, messageText);
  }

  @Override
  public void callbackProcessing(Update update) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    String data = update.getCallbackQuery().getData();

    switch (data) {
      case Commands.FIRST -> {
        sendMessage(chatId, TEXT.FIRST_STEP.get(), keyboard(
            button("Следующий шаг", Commands.SECOND)
        ));
      }
      case Commands.SECOND -> {
        sendMessage(chatId, TEXT.SECOND_STEP.get(), null);
        currentState = 1;
      }
    }
  }

  private void stateProcessing(Long chatId, String messageText) {
    switch (currentState) {
      case 0 -> sendMessage(chatId, TEXT.ERROR.get(), null);
    }
  }

  // COMMANDS ===========================================
  private void startCommand(Long chatId, String firstName) {
    sendMessage(chatId, TEXT.START.format(firstName != null ? firstName : "друг"),
        keyboard(button("Приступим?", Commands.FIRST)));
  }

  private void broadcastCommand(String messageText, Long chatId) {
    // Для безопасности — разрешим только админскую команду
    Long adminId = props.getAdminChatId();
    if (adminId != null && adminId.equals(chatId)) {
      String body = messageText.substring("/broadcast ".length());
      broadcastService.broadcast(body);
    } else {
      sendMessage(chatId, TEXT.BROADCAST_FAIL.get(), null);
    }
  }

  private void unsubscribeCommand(Long chatId) {
    subscriberService.unsubscribe(chatId);
    sendMessage(chatId, TEXT.UNSUBSCRIBE.get(), null);
  }
}

