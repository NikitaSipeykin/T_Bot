package app.core.program;


import java.time.LocalDate;

public record ProgramStartInfo(
    Long chatId,
    LocalDate startDate
) {}