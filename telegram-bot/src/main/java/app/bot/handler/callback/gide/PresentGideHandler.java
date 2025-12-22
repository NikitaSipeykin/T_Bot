package app.bot.handler.callback.gide;

import app.bot.bot.Commands;
import app.bot.bot.responce.SendWithDelayedResponse;
import app.bot.bot.responce.*;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PresentGideHandler implements CallbackHandler {

  private final BotTextService textService;

  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.PRESENT_GIDE);
  }

  @Override
  public BotResponse handle(CallbackQuery callbackQuery) {
    Long chatId = callbackQuery.getMessage().getChatId();
    CompositeResponse response = new CompositeResponse(new ArrayList<>());
    CompositeResponse delayedResponse = new CompositeResponse(new ArrayList<>());

    TextResponse text = new TextResponse(chatId, textService.get(TextMarker.PRESENT_GIDE), null);
    MediaResponse doc = new MediaResponse(chatId, MediaType.DOCUMENT, Commands.DOC_GIDE);

    TextResponse delayedText = new TextResponse(chatId, textService.get(TextMarker.READY_TO_GIDE),
        KeyboardFactory.from(List.of(new KeyboardOption("Да, проверим чакры!", TextMarker.CHAKRA_INTRO))));

    response.responses().add(text);
    response.responses().add(doc);
    delayedResponse.responses().add(delayedText);

    return new SendWithDelayedResponse(response, delayedResponse, Duration.ofSeconds(30));
  }
}
