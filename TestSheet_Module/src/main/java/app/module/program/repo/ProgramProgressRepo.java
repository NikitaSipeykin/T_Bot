package app.module.program.repo;

import app.module.program.dao.ProgramProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProgramProgressRepo extends JpaRepository<ProgramProgress, Long> {
  boolean existsByChatId(Long chatId);
  Optional<ProgramProgress> findByChatId(Long chatId);
}

