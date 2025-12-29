package app.module.program;

import app.core.payment.AccessService;
import app.core.payment.PaidPaymentInfo;
import app.core.program.CompositeProgramMessage;
import app.core.program.ProgramResponse;
import app.core.test.AnswerOption;
import app.core.program.DailyUpdateResult;
import app.core.program.ProgramMessage;
import app.module.node.texts.TextMarker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProgramService implements AccessService {
  private final ProgramProgressService progressService;


  public ProgramService(ProgramProgressService progressService) {
    this.progressService = progressService;
  }

  public ProgramResponse startProgram(Long chatId) {
    if (!progressService.isUserInProgram(chatId)) {
      ProgramResponse message = getMessage(chatId);
      if (message != null) return message;
    }
    return null;
  }

  public ProgramResponse nextMessage(Long chatId) {
    if (progressService.isUserInProgram(chatId)) {
      ProgramResponse message = getMessage(chatId);
      log.info("ProgramResponse message = " + message);
      if (message != null) return message;
    }
    log.error("Не удалось отправить сообщение");
    return null;
  }

  private ProgramResponse getMessage(Long chatId) {
    String currentBlock = progressService.getCurrentBlock(chatId);
    boolean canAccess = progressService.canUserAccessBlock(chatId, currentBlock);
    List<AnswerOption> options = List.of();

    if (currentBlock.endsWith(TextMarker.BEGIN_MARKER)) {
      String button = progressService.getCurrentButton(chatId);
      options = List.of(new AnswerOption(0L, button, TextMarker.PROGRAM));
      progressService.moveToNextBlock(chatId);
      return new ProgramMessage(currentBlock, options, false);
    }

    log.info("canAccess = " + canAccess);
    if (canAccess) {
      currentBlock = progressService.getCurrentBlock(chatId);
      //with buttons
      if (currentBlock.endsWith(TextMarker.INTRO_MARKER) || currentBlock.endsWith(TextMarker.PRACTICE_INTRO_MARKER) ||
          currentBlock.endsWith(TextMarker.END_MARKER)) {
        log.info("into button msg");

        String button = progressService.getCurrentButton(chatId);
        if (currentBlock.endsWith(TextMarker.PROGRAM_SAHASRARA_END)){
          options = List.of(new AnswerOption(0L, button, TextMarker.END_PROGRAM));
        }else {
          options = List.of(new AnswerOption(0L, button, TextMarker.PROGRAM));
        }

      }
      //without buttons
      else if (currentBlock.endsWith(TextMarker.QUESTIONS_MARKER)) {
        CompositeProgramMessage compositeProgramMessage = new CompositeProgramMessage(new ArrayList<>());
        progressService.moveToNextBlock(chatId);
        ProgramMessage messageOne = new ProgramMessage(currentBlock, options, true);
        ProgramMessage messageTwo = new ProgramMessage(progressService.getCurrentBlock(chatId), options, true);

        compositeProgramMessage.responses().add(messageOne);
        compositeProgramMessage.responses().add(messageTwo);
        progressService.moveToNextBlock(chatId);

        return compositeProgramMessage;
      }
      progressService.moveToNextBlock(chatId);
    }
    //limit
    else {
      log.info("inside TODAY_LIMIT");
      currentBlock = TextMarker.TODAY_LIMIT;
    }
    return new ProgramMessage(currentBlock, options, false);
  }

  public List<DailyUpdateResult> dailyUpdate() {
    return progressService.dailyUpdate();
  }

  public void moveToTopic(Long chatId, String topicName){
    progressService.setProgress(chatId, topicName);
  }

  public boolean isLimitReached(Long chatId){
    String currentBlock = progressService.getCurrentBlock(chatId);
    return !progressService.canUserAccessBlock(chatId, currentBlock);
  }

  public boolean checkUserAccessProgram(Long chatId) {
    return progressService.isUserInProgram(chatId);
  }

  @Override
  public void grantAccess(PaidPaymentInfo payment) {
    progressService.createIfNotExists(
        payment.chatId(),
        payment.paymentId()
    );
  }
}
