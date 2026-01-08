package app.module.program.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "daily_limits")
@Slf4j
public class DailyLimit {

  @Id
  @Column(name = "day_number")
  private Integer dayNumber;

  @Column(name = "limit_block", nullable = false)
  private String limitBlock;

  // Getters / Setters

  public Integer getDayNumber() {
    return dayNumber;
  }

  public String getLimitBlock() {
    log.debug("limitBlock = " + limitBlock);
    return limitBlock;
  }
}

