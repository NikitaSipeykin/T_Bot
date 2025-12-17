package app.module.program.repo;

import app.module.program.dao.DailyLimit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyLimitRepo extends JpaRepository<DailyLimit, Integer> {
  DailyLimit findByDayNumber(int days);
}
