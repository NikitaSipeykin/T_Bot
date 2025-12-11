package app.module.program;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class ProgramDayCalculator {

  public int getDaysSincePayment(LocalDate paymentDate) {
    return (int) ChronoUnit.DAYS.between(paymentDate, LocalDate.now()) + 1;
  }
}

