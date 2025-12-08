package app.core;

import java.util.List;

public record FinalMessage(
    String text,
    List<String> recommendedTopicNames
) {}


