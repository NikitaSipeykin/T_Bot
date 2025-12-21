package app.bot.sender;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.CompositeResponse;
import app.bot.bot.responce.MediaResponse;
import app.bot.bot.responce.TextResponse;
import app.module.node.NoteService;
import app.module.node.web.MediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@Component
@Slf4j
public class TelegramSender {
  private final ObjectProvider<TelegramLongPollingBot> botProvider;
  private final NoteService noteService;
  private final MediaService mediaService;

  public TelegramSender(ObjectProvider<TelegramLongPollingBot> botProvider,
                        NoteService noteService, MediaService mediaService) {
    this.botProvider = botProvider;
    this.noteService = noteService;
    this.mediaService = mediaService;
  }

  public void send(BotResponse response) {

    if (response instanceof TextResponse r) {
      sendText(r);

    } else if (response instanceof MediaResponse r) {
      sendMedia(r);

    } else if (response instanceof CompositeResponse r) {
      r.responses().forEach(this::send);
    }
  }


  private void sendText(TextResponse r) {
    SendMessage message = new SendMessage(r.chatId().toString(), r.text());

    if (r.keyboard() != null && !r.keyboard().getKeyboard().isEmpty()) {
      InlineKeyboardMarkup markup = new InlineKeyboardMarkup(r.keyboard().getKeyboard());
      message.setReplyMarkup(markup);
    }

    try {
      botProvider.getObject().execute(message);
    } catch (TelegramApiException e) {
      log.error("Error sending message to chatId={}", r.chatId(), e);
    }
  }


  private void sendMedia(MediaResponse r) {

    switch (r.type()) {
      case VOICE -> {
        try {
          File voice = mediaService.getFileByKey(r.fileId());

          var sendVoiceNote = noteService.buildVoice(r.chatId(), voice);
          botProvider.getObject().execute(sendVoiceNote);
        } catch (Exception e) {
          log.error("Не удалось отправить голосовое сообщение chatId={}", r.chatId(), e);
        }
      }

      case VIDEO_NOTE -> {
        try {
          File videoNote = mediaService.getFileByKey(r.fileId());

          var sendVideoNote = noteService.buildVideoNote(r.chatId(), videoNote);
          botProvider.getObject().execute(sendVideoNote);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      case DOCUMENT -> {
        try {
          File doc = mediaService.getFileByKey(r.fileId());

          var sendDocument = noteService.buildPdf(r.chatId(), doc);
          botProvider.getObject().execute(sendDocument);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      case AUDIO -> {
        try {
          File audio = mediaService.getFileByKey(r.fileId());

          var sendAudioNote = noteService.buildAudio(r.chatId(), audio);
          botProvider.getObject().execute(sendAudioNote);

        } catch (Exception e) {
          log.error("Не удалось отправить аудио chatId={}", r.chatId(), e);
        }
      }

    }
  }

}

