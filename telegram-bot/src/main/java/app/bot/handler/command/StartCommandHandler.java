package app.bot.handler.command;

import app.bot.bot.Commands;
import app.bot.bot.responce.*;
import app.bot.facade.AnalyticsFacade;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.broadcast.SubscriberService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.node.web.MediaService;
import app.module.program.ProgramService;
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

  private final BotTextService textService;
  private final UserStateService userStateService;
  private final SubscriberService subscriberService;
  private final ProgramService programService;
  private final AnalyticsFacade analytics;

  @Override
  public String command() {
    return "/start";
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    String username = message.getFrom().getUserName();
    String firstName = message.getFrom().getFirstName();

    if (programService.checkUserAccessProgram(chatId)) {
      analytics.trackSubscribe(message, true);
      analytics.trackBlockView(chatId, "PROGRAM_CONTINUE");

      CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

      TextResponse text = new TextResponse(chatId,
          textService.format("START", firstName != null ? firstName : "друг"), null);

      TextResponse programText = new TextResponse(chatId, "Тебе уже доступен курс. Давай продолжим?",
          KeyboardFactory.from(Collections.singletonList(new
              KeyboardOption("Хорошо!", TextMarker.PROGRAM))));

      compositeResponse.responses().add(text);
      compositeResponse.responses().add(programText);

      return compositeResponse;
    }

    subscriberService.subscribe(chatId, username, firstName);
    userStateService.setState(chatId, UserState.DEFAULT);

    analytics.trackSubscribe(message, false);
    analytics.trackBlockView(chatId, "START");

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    TextResponse text = new TextResponse(chatId,
        textService.format("START", firstName != null ? firstName : "друг"),
        KeyboardFactory.from(Collections.singletonList(new
            KeyboardOption("Да!", TextMarker.PRESENT_GIDE))));

    MediaResponse video = new MediaResponse(chatId, MediaType.VIDEO_NOTE, Commands.VIDEO_START);

    compositeResponse.responses().add(text);
    compositeResponse.responses().add(video);

    return compositeResponse;
  }
}

