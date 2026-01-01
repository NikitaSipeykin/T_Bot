package app.module.program;

import app.core.program.DailyUpdateResult;
import app.core.payment.PaymentQueryService;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramProgressServiceImpl implements ProgramProgressService {

  private final ProgramProgressRepo progressRepo;
  private final ProgramBlocksRepo blocksRepo;
  private final DailyLimitRepo dailyLimitRepo;

  private final PaymentQueryService paymentQueryService;

  // ============================================================
  // Creating the note
  // ============================================================

  @Override
  public void createIfNotExists(Long chatId, Long paymentId) {
    progressRepo.findById(chatId).orElseGet(() -> {
      ProgramProgress p = new ProgramProgress();
      p.setChatId(chatId);
      p.setPaymentId(paymentId);
      p.setProgressLevel("PROGRAM_BEGIN");
      return progressRepo.save(p);
    });
  }

  // ============================================================
  // Helper
  // ============================================================

  private LocalDate resolvePaymentDate(ProgramProgress progress) {
    return paymentQueryService
        .getPaidPayment(progress.getPaymentId())
        .orElseThrow(() ->
            new IllegalStateException(
                "Paid payment not found for paymentId=" + progress.getPaymentId()
            )
        )
        .paidAt()
        .toLocalDate();
  }

  // ============================================================
  // Get the progress
  // ============================================================

  @Override
  public ProgramProgressDto getProgress(Long chatId) {
    ProgramProgress p = progressRepo.findById(chatId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    LocalDate paymentDate = resolvePaymentDate(p);
    return new ProgramProgressDto(chatId, paymentDate, p.getProgressLevel());
  }

  @Override
  public boolean isUserInProgram(Long chatId) {
    return progressRepo.existsByChatId(chatId);
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

    return blocksRepo.findByName(p.getProgressLevel())
        .map(ProgramBlocks::getButtonText)
        .orElse("");
  }

  // ============================================================
  // Dynamic limit bloc
  // ============================================================

  @Override
  public String getTodayLimit(Long chatId) {
    ProgramProgress p = progressRepo.findById(chatId)
        .orElseThrow(() -> new RuntimeException("Not found"));

    LocalDate paymentDate = resolvePaymentDate(p);
    long days = DAYS.between(paymentDate, LocalDate.now()) + 1;

    DailyLimit limit = dailyLimitRepo.findByDayNumber((int) days);
    return limit != null ? limit.getLimitBlock() : findMaxAvailableLimit();
  }

  // ============================================================
  // Block access checking
  // ============================================================

  @Override
  public boolean canUserAccessBlock(Long chatId, String blockName) {
    String todayLimitName = getTodayLimit(chatId);
    Optional<ProgramBlocks> limitBlock = blocksRepo.findByName(todayLimitName);
    Optional<ProgramBlocks> currentBlock = blocksRepo.findByName(blockName);

    log.info("canUserAccessBlock() \n" +
             "chatId = " + chatId + "\n" +
             "blockName = " + blockName + "\n" +
             "todayLimitName = " + todayLimitName);

    log.info("\nlimitBlock = " + limitBlock + ";\n" +
             "currentBlock = " + currentBlock);

    return limitBlock.get().getId() >= currentBlock.get().getId();
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
        .map(p -> new ProgramProgressDto(p.getChatId(), resolvePaymentDate(p), p.getProgressLevel()))
        .toList();
  }

  // ============================================================
  // Daily update
  // ============================================================
  @Override
  public List<DailyUpdateResult> dailyUpdate() {
    List<DailyUpdateResult> results = new ArrayList<>();

    for (ProgramProgress p : progressRepo.findAll()) {

      LocalDate paymentDate = resolvePaymentDate(p);
      long days = DAYS.between(paymentDate, LocalDate.now());

      DailyLimit limit = dailyLimitRepo.findByDayNumber((int) days + 1);
      if (limit == null) continue;

      ProgramBlocks current = blocksRepo.findByName(p.getProgressLevel()).orElse(null);
      ProgramBlocks limitBlock = blocksRepo.findByName(limit.getLimitBlock()).orElse(null);

      if (current == null || limitBlock == null) continue;
      if (current.getId() >= limitBlock.getId()) continue;

      results.add(new DailyUpdateResult(p.getChatId(), limit.getLimitBlock()));
    }
    return results;
  }

  //todo: check validation of blocks
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
