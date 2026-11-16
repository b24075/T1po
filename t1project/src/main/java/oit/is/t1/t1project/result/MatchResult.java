package oit.is.t1.t1project.result;

public record MatchResult(
    String matchId,
    String mode,
    String firstPlayer,
    String secondPlayer,
    String winner) {
}