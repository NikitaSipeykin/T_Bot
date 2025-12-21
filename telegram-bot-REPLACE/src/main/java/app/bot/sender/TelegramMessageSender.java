package app.bot.sender;

import app.core.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramMessageSender implements MessageSender {

  private final TelegramExecutor executor;

  @Override
  public void sendText(Long chatId, String text) {
    SendMessage msg = SendMessage.builder()
        .chatId(chatId.toString())
        .text(text)
        .build();

    sendMessage(msg);
  }

  public void sendMessage(SendMessage message) {
    executor.execute(message);
  }

  public void editReplyMarkup(EditMessageReplyMarkup edit) {
    executor.execute(edit);
  }

  public void sendInvoice(SendInvoice invoice) {
    executor.execute(invoice);
  }

  public void answerPreCheckout(AnswerPreCheckoutQuery answer) {
    executor.execute(answer);
  }
}
