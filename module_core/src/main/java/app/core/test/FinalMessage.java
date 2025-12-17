package app.core.test;

import java.util.List;

public record FinalMessage(
    String text,
    List<String> recommendedTopicNames
) {}


