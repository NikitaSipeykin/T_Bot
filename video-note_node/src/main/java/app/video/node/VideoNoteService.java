package app.video.node;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

@Service
public class VideoNoteService {

  public SendVideoNote buildVideoNote(Long chatId, File videoFile) {
    SendVideoNote note = new SendVideoNote();
    note.setChatId(chatId);
    note.setVideoNote(new InputFile(videoFile));

    // опционально
    // note.setDuration(30);

    return note;
  }
}