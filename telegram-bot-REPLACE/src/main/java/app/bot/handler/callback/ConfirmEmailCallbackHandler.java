package app.bot.handler.callback;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class ConfirmEmailCallbackHandler implements CallbackHandler {

  @Override
  public boolean supports(String callbackData) {
    return callbackData.startsWith("EMAIL_CONFIRM:");
  }

  @Override
  public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
    return EditMessageText.builder()
        .chatId(callbackQuery.getMessage().getChatId())
        .messageId(callbackQuery.getMessage().getMessageId())
        .text("✅ Email подтверждён")
        .build();
  }
}
