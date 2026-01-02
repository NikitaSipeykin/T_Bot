package app.bot.handler.message.course;

import app.bot.bot.responce.*;
import app.bot.config.BotProperties;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.message.MessageHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.program.CompositeProgramMessage;
import app.core.program.ProgramMessage;
import app.module.chat.service.ChatHistoryService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.program.ProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminQuestionMessageHandler  implements MessageHandler {

  private final ProgramService programService;
  private final BotTextService textService;
  private final UserStateService userStateService;
  private final ChatHistoryService chatHistoryService;

  private final BotProperties botProperties;
  private final AnalyticsFacade analyticsFacade;


  @Override
  public UserState supports() {
    return UserState.REQUEST;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    String userText = message.getText();

    if (!programService.checkUserAccessProgram(chatId)) {
      userStateService.setState(chatId, UserState.DEFAULT);
      return new TextResponse(chatId, "Данная комманда доступна после начала курса", null);
    }

    userStateService.setState(chatId, UserState.COURSE);

    chatHistoryService.logUserMessage(chatId, userText);

    String adminText = """
      ❓ Новый вопрос от пользователя
      
      Chat ID: %d
      Username: @%s
      
      Текст:
      %s
      """.formatted(
        chatId,
        message.getFrom().getUserName(),
        userText
    );

    analyticsFacade.adminQuestionSent(chatId);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    Long adminId = botProperties.getAdminId();

    TextResponse textForAdmin = new TextResponse(adminId, adminText, null);
    TextResponse textForUser = new TextResponse(chatId, "Запрос к админу отправлен!", null);

    compositeResponse.responses().add(textForAdmin);
    compositeResponse.responses().add(textForUser);

    return compositeResponse;
  }
}
