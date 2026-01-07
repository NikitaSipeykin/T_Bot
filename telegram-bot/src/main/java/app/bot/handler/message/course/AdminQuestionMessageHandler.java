package app.bot.handler.message.course;

import app.bot.bot.responce.*;
import app.bot.config.BotProperties;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.message.MessageHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
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
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminQuestionMessageHandler implements MessageHandler {

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
    Long userId = message.getFrom().getId();

    if (userId.equals(botProperties.getAdminId())) {
      String text = message.getText();

      Pattern pattern = Pattern.compile("^\\s*(\\d+)\\s+(.*)$");
      Matcher matcher = pattern.matcher(text);

      if (!matcher.matches()) {
        return new TextResponse(chatId, "Неверный формат сообщения.\nИспользуй: <userId> <текст>",
            KeyboardFactory.from(Collections.singletonList(new
                KeyboardOption("Не хочу отправлять сообщение.", "QUESTION_OUT"))));
      }

      Long recipientChatId = Long.parseLong(matcher.group(1));

      if (!programService.checkUserAccessProgram(recipientChatId)){
        return new TextResponse(chatId, "Такой пользователь еще не начинал общение с ботом!",
            KeyboardFactory.from(Collections.singletonList(new
                KeyboardOption("Не хочу отправлять сообщение.", "QUESTION_OUT"))));
      }

      userStateService.setState(chatId, UserState.DEFAULT);
      String replyText = matcher.group(2);

      chatHistoryService.logBotMessage(recipientChatId, replyText);

      CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());
      TextResponse textForCustomer = new TextResponse(recipientChatId, replyText, null);
      TextResponse textForAdmin = new TextResponse(chatId, "Текст отправлен пользователю - " + recipientChatId, null);

      compositeResponse.responses().add(textForCustomer);
      compositeResponse.responses().add(textForAdmin);

      return compositeResponse;
    }

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
