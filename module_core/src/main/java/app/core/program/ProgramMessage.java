package app.core.program;

import app.core.test.AnswerOption;

import java.util.List;

public record ProgramMessage(
    String text,
    List<AnswerOption> options,
    boolean shouldBeNext
){}
