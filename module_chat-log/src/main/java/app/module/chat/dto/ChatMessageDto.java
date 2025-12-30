package app.module.chat.dto;

import app.module.chat.SenderType;

import java.time.Instant;
import java.util.List;

public class ChatMessageDto {

  public SenderType senderType;

  public String text;
  public Instant createdAt;
}
