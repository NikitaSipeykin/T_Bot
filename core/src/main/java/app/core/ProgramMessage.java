package app.core;

import java.util.List;

public record ProgramMessage(
    String text,
    List<AnswerOption> options,
    boolean shouldBeNext
){}
