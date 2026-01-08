package app.bot.handler.command;

import app.bot.bot.responce.*;
import app.bot.config.BotProperties;
import app.bot.facade.AnalyticsFacade;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.module.node.texts.TextMarker;
import app.module.program.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;


@Component
@RequiredArgsConstructor
public class AdminQuestionCommandHandler implements CommandHandler {

  private final UserStateService userStateService;
  private final ProgramService programService;

  private final BotProperties botProperties;
  private final AnalyticsFacade analytics;

  @Override
  public String command() {
    return "/request";
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    Long userId = message.getFrom().getId();

    if (userId.equals(botProperties.getAdminId())) {
      userStateService.setState(chatId, UserState.REQUEST);
      return new TextResponse(chatId, "Для отправки сообщения конкретному пользователю укажите его Chat ID в начале сообщения. \n" +
                                      "пример: 123456789 Привет!", null);
    }

    if (programService.checkUserAccessProgram(chatId)) {
      userStateService.setState(chatId, UserState.REQUEST);
      return new TextResponse(chatId, "Следующее сообщение будет отправлено напрямую админу!", null);
    }
    return new TextResponse(chatId, "Данная комманда доступна после начала курса", null);
  }
}

