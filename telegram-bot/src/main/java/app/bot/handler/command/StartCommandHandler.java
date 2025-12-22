package app.bot.handler.command;

import app.bot.bot.Commands;
import app.bot.bot.responce.*;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserStateService;
import app.core.broadcast.SubscriberService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.node.web.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {

  private final SubscriberService subscriberService;
  private final BotTextService textService;
  private final UserStateService userStateService;
  private final MediaService mediaService;

  @Override
  public String command() {
    return "/start";
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    String firstName = message.getFrom().getFirstName();
    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    TextResponse text = new TextResponse(
        chatId,
        textService.format("START", firstName != null ? firstName : "друг"),
        KeyboardFactory.from(Collections.singletonList(new
            KeyboardOption("Да!", TextMarker.PRESENT_GIDE))));

    compositeResponse.responses().add(text);

    MediaResponse video = new MediaResponse(
        chatId,
        MediaType.VIDEO_NOTE,
        Commands.VIDEO_START);
    compositeResponse.responses().add(video);

    return compositeResponse;
  }
}

