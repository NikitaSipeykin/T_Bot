package app.bot.demo;

import app.bot.BaseTelegramBot;
import app.bot.TelegramMessageSender;
import app.text.node.texts.BotTextService;
import app.core.BroadcastService;
import app.core.SubscriberService;
import app.video.node.VideoNoteService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;

@Component
public class DemoBot extends BaseTelegramBot {
  private final SubscriberService subscriberService;
  private final BroadcastService broadcastService;
  private final VideoNoteService videoNoteService;
  private final BotTextService text;

  private int currentState = 0;


  public DemoBot(SubscriberService subscriberService, BotTextService text, VideoNoteService videoNoteService) {
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
    }
    else if (messageText.equals(Commands.CIRCLE)) {
      circleCommand(chatId, update);
    }
    else if (messageText.equals(Commands.UNSUBSCRIBE)) {
      unsubscribeCommand(chatId);
    }
    else if (messageText.startsWith(Commands.BROADCAST)) {
      broadcastCommand(chatId, messageText, userId);
    }
    else stateProcessing(chatId, messageText);
  }

  @Override
  public void callbackProcessing(Update update) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    String data = update.getCallbackQuery().getData();

    switch (data) {
      case Commands.FIRST -> {
        sendMessage(chatId, text.get("FIRST_STEP"), keyboard(
            button("Следующий шаг", Commands.SECOND)
        ));
      }
      case Commands.SECOND -> {
        sendMessage(chatId, text.get("SECOND_STEP"), null);
        currentState = 1;
      }
    }
  }

  private void stateProcessing(Long chatId, String messageText) {
    switch (currentState) {
      case 0 -> sendMessage(chatId, text.get("ERROR"), null);
    }
  }

  // COMMANDS ===========================================
  private void startCommand(Long chatId, String firstName) {
    sendMessage(chatId, text.format("START", firstName != null ? firstName : "друг"),
        keyboard(button("Приступим?", Commands.FIRST)));
  }

  private void circleCommand(Long chatId, Update update) {
    try {
      String resourceName = "video.mp4";

      InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
      if (is == null) {
        sendMessage(chatId, "Файл ресурса не найден: " + resourceName, null);
        return;
      }

      File temp = File.createTempFile("circle", ".mp4");
      temp.deleteOnExit();

      // Копируем ресурс из JAR во временный файл
      try (FileOutputStream fos = new FileOutputStream(temp)) {
        is.transferTo(fos);
      }

      // Отправляем кружок
      var sendVideoNote = videoNoteService.buildVideoNote(chatId, temp);
      execute(sendVideoNote);

      execute(sendVideoNote);
    } catch (Exception e) {
      e.printStackTrace();
      sendMessage(update.getMessage().getChatId(), "Не удалось отправить кругетс", null);
    }
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

