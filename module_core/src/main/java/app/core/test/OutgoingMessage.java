package app.core.test;

import java.util.List;

public record OutgoingMessage(
    String text,
    List<AnswerOption> options,
    boolean isNextTopic
) {}


