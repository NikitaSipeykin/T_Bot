package app.bot.bot.responce;

import java.util.List;

public record CompositeResponse(
    List<BotResponse> responses
) implements BotResponse {}

