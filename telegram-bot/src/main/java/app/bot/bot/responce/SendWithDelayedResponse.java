package app.bot.bot.responce;


import java.time.Duration;

public record SendWithDelayedResponse(
    CompositeResponse responsesNow,
    CompositeResponse responsesDelayed,
    Duration delay
) implements BotResponse {}