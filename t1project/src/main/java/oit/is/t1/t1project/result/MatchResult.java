package oit.is.t1.t1project.result;

import java.time.LocalDateTime;

public record MatchResult(
    String matchId,
    String mode,
    String firstPlayer,
    String secondPlayer,
    String winner,
    LocalDateTime finishedAt
) {
}
