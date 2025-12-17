package app.module.program;

import app.core.payment.AccessService;
import app.core.payment.PaidPaymentInfo;
import app.core.test.AnswerOption;
import app.core.program.DailyUpdateResult;
import app.core.program.ProgramMessage;
import app.module.node.texts.TextMarker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProgramService implements AccessService {
  private final ProgramProgressService progressService;


  public ProgramService(ProgramProgressService progressService) {
    this.progressService = progressService;
  }

  public ProgramMessage startProgram(Long chatId){
    //Todo: debug (change after add payment)
    if (!progressService.isUserInProgram(chatId)){

      ProgramMessage message = getMessage(chatId);

      if (message != null) return message;
    }
    return null;
  }

  public ProgramMessage nextMessage(Long chatId) {
    if (progressService.isUserInProgram(chatId)){
      ProgramMessage message = getMessage(chatId);
      if (message != null) return message;
    }
    log.error("Не удалось отправить сообщение");
    return null;
  }

  private ProgramMessage getMessage(Long chatId) {
    String currentBlock = progressService.getCurrentBlock(chatId);
    boolean canAccess = progressService.canUserAccessBlock(chatId, currentBlock);
    List<AnswerOption>  options = List.of();

    if (currentBlock.endsWith(TextMarker.BEGIN_MARKER)){
      String button = progressService.getCurrentButton(chatId);
      options = List.of(new AnswerOption(0L, button, TextMarker.PROGRAM));
      progressService.moveToNextBlock(chatId);
      return new ProgramMessage(currentBlock, options,  false);
    }

    log.info("canAccess = " + canAccess);
    if (canAccess){
      currentBlock = progressService.getCurrentBlock(chatId);
      //with buttons
      if ( currentBlock.endsWith(TextMarker.INTRO_MARKER) || currentBlock.endsWith(TextMarker.PRACTICE_INTRO_MARKER) ||
           currentBlock.endsWith(TextMarker.END_MARKER)) {

        String button = progressService.getCurrentButton(chatId);
        options = List.of(new AnswerOption(0L, button, TextMarker.PROGRAM));
      }
      //without buttons
      else if (currentBlock.endsWith(TextMarker.QUESTIONS_MARKER)){
        progressService.moveToNextBlock(chatId);
          return new ProgramMessage(currentBlock, options,  true);
      }
      progressService.moveToNextBlock(chatId);
    }
    //limit
    else {
      log.info("inside TODAY_LIMIT");
      currentBlock = TextMarker.TODAY_LIMIT;
    }
    return new ProgramMessage(currentBlock, options,  false);
  }

  public List<DailyUpdateResult> dailyUpdate() {
    return progressService.dailyUpdate();
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
