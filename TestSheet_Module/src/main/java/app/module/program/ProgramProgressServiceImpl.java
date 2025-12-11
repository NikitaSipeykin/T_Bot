package app.module.program;

import app.core.DailyUpdateResult;
import app.module.program.dao.DailyLimit;
import app.module.program.dao.ProgramBlocks;
import app.module.program.dao.ProgramProgress;
import app.module.program.repo.DailyLimitRepo;
import app.module.program.repo.ProgramBlocksRepo;
import app.module.program.repo.ProgramProgressRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgramProgressServiceImpl implements ProgramProgressService {

  private final ProgramProgressRepo progressRepo;
  private final ProgramBlocksRepo blocksRepo;
  private final DailyLimitRepo dailyLimitRepo;


  // ============================================================
  // Creating the note
  // ============================================================

  @Override
  public void createIfNotExists(Long chatId, LocalDate paymentDate) {
    log.info("createIfNotExists");
    progressRepo.findById(chatId).orElseGet(() -> {
      ProgramProgress p = new ProgramProgress();
      p.setChatId(chatId);
      p.setPaymentDate(paymentDate);
      p.setProgressLevel("PROGRAM_BEGIN");
      return progressRepo.save(p);
    });
  }

  // ============================================================
  // Get the progress
  // ============================================================

  @Override
  public ProgramProgressDto getProgress(Long chatId) {
    ProgramProgress p = progressRepo.findById(chatId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    return new ProgramProgressDto(p.getChatId(), p.getPaymentDate(), p.getProgressLevel());
  }

  @Override
  public boolean isUserInProgram(Long chatId) {
    return progressRepo.existsById(chatId);
  }

  @Override
  public String getCurrentBlock(Long chatId) {
    ProgramProgress p = progressRepo.findById(chatId)
        .orElseThrow(() -> new RuntimeException("Not found"));

    return p.getProgressLevel();
  }

  @Override
  public String getCurrentButton(Long chatId){
    ProgramProgress p = progressRepo.findById(chatId)
        .orElseThrow(() -> new RuntimeException("Not found"));

    log.info("current block is = "+ blocksRepo.findByName(p.getProgressLevel()).get().getButtonText());
    return blocksRepo.findByName(p.getProgressLevel()).get().getButtonText();
  }

  // ============================================================
  // Dynamic limit bloc
  // ============================================================

  @Override
  public String getTodayLimit(Long chatId) {
    ProgramProgress p = progressRepo.findById(chatId)
        .orElseThrow(() -> new RuntimeException("Not found"));

    long days = DAYS.between(p.getPaymentDate(), LocalDate.now()) + 1;

    DailyLimit lim = dailyLimitRepo.findByDayNumber((int) days);
    log.info("days = " + days + "; lim = " + lim);
    if (lim == null) {return findMaxAvailableLimit();} // если дней больше чем есть лимитов

    return lim.getLimitBlock();
  }

  // ============================================================
  // Block access checking
  // ============================================================

  @Override
  public boolean canUserAccessBlock(Long chatId, String blockName) {
    String todayLimit = getTodayLimit(chatId);
    if (todayLimit == null) return false;
    log.info("\nblockName = " + blockName + ";\n" +
             "todayLimit = " + todayLimit + ";\n" );
    return isBeforeOrEqual(blockName, todayLimit);
  }

  private boolean isBeforeOrEqual(String block, String limit) {
    log.info("isBeforeOrEqual inside");
    Map<String, ProgramBlocks> all = blocksRepo.findAll()
        .stream().collect(Collectors.toMap(ProgramBlocks::getName, b -> b));

    ProgramBlocks currentBlock = all.get(block);
    ProgramBlocks currentLimitBlock = all.get(limit);
    while (currentBlock != null) {
      if (currentBlock.getName().equals(limit)) {
        return true;
      }
      if (currentBlock.getId().equals(currentLimitBlock.getId() + 1)){
        return false;
      }
      currentBlock = all.get(currentBlock.getNextBlock());
    }
    log.info("return false");
    return false;
  }


  // ============================================================
  // Delete progress
  // ============================================================

  @Override
  public void moveToNextBlock(Long chatId) {
    ProgramProgress p = progressRepo.findById(chatId)
        .orElseThrow(() -> new RuntimeException("Not found"));

    ProgramBlocks current = blocksRepo.findByName(p.getProgressLevel())
        .orElseThrow(() -> new RuntimeException("Block not found"));

    p.setProgressLevel(current.getNextBlock());
    progressRepo.save(p);
  }

  @Override
  public void setProgress(Long chatId, String blockName) {
    ProgramProgress p = progressRepo.findById(chatId)
        .orElseThrow(() -> new RuntimeException("Not found"));

    p.setProgressLevel(blockName);
    progressRepo.save(p);
  }

  @Override
  public List<ProgramProgressDto> getAllProgresses() {
    return progressRepo.findAll()
        .stream()
        .map(p -> new ProgramProgressDto(p.getChatId(), p.getPaymentDate(), p.getProgressLevel()))
        .toList();
  }

  // ============================================================
  // Daily update
  // ============================================================

  @Override
  public List<DailyUpdateResult> dailyUpdate() {
    log.info("PPSI dailyUpdate");
    List<DailyUpdateResult> results = new ArrayList<>();

    for (ProgramProgress p : progressRepo.findAll()) {

      long days = DAYS.between(p.getPaymentDate(), LocalDate.now());

      log.info("getPaymentDate = " + p.getPaymentDate() + "\n" +
               "LocalDate = " +  LocalDate.now() + "\n" +
               "days = " + days);

      DailyLimit limit = dailyLimitRepo.findByDayNumber((int) days + 1);
      if (limit == null) continue;

      String todayLimit = limit.getLimitBlock();

      Optional<ProgramBlocks> currentBlock = blocksRepo.findByName(p.getProgressLevel());
      Optional<ProgramBlocks> limitBlock = blocksRepo.findByName(todayLimit);

      if (currentBlock.get().getId() >= limitBlock.get().getId()){
        continue;
      }

      results.add(new DailyUpdateResult(p.getChatId(), todayLimit));
    }

    return results;
  }

  private boolean isBlockBeforeOrEqual(String block, String limit) {
    String current = block;

    while (current != null) {
      if (current.equals(limit)) return true;

      ProgramBlocks b = blocksRepo.findByName(current).orElse(null);
      if (b == null) break;

      current = b.getNextBlock();
    }
    return false;
  }

  private String findMaxAvailableLimit() {
    // fallback если пользователь прошёл больше дней, чем лимитов
    log.info("findMaxAvailableLimit" + dailyLimitRepo.findAll().stream()
        .max(Comparator.comparingInt(DailyLimit::getDayNumber))
        .map(DailyLimit::getLimitBlock)
        .orElse(null));
    return dailyLimitRepo.findAll().stream()
        .max(Comparator.comparingInt(DailyLimit::getDayNumber))
        .map(DailyLimit::getLimitBlock)
        .orElse(null);
  }
}
