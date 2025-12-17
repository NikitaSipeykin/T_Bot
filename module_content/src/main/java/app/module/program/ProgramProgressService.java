package app.module.program;

import app.core.program.DailyUpdateResult;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;

public interface ProgramProgressService {

  ProgramProgressDto getProgress(Long chatId);

  void createIfNotExists(Long chatId, Long paymentId);

  String getCurrentBlock(Long chatId);

  boolean canUserAccessBlock(Long chatId, String blockName);

  void moveToNextBlock(Long chatId);

  void setProgress(Long chatId, String blockName);

  String getCurrentButton(Long chatId);

  String getTodayLimit(Long chatId);

  List<ProgramProgressDto> getAllProgresses();

  List<DailyUpdateResult> dailyUpdate();

  boolean isUserInProgram(Long chatId);

  @Scheduled(cron = "0 0 12 * * *") // каждый день в 05:00
  default void scheduledDailyUpdate(){
    dailyUpdate();
  }
}

