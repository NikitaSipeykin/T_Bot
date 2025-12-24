package app.core.program;

import java.util.List;

public record CompositeProgramMessage(
    List<ProgramMessage> responses
) implements ProgramResponse {}
