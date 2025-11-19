package app.bot;

import lombok.Data;

@Data
public class InternalSendRequest {
  private Long chatId;
  private String text;
}
