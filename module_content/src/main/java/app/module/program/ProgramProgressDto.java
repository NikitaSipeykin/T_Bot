package app.module.program;

import app.module.program.dao.ProgramProgress;

import java.time.LocalDate;

public record ProgramProgressDto(
    Long chatId,
    LocalDate paymentDate,
    String progressLevel
) {}

