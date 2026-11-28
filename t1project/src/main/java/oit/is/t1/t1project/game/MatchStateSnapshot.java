package oit.is.t1.t1project.game;

public record MatchStateSnapshot(
    String matchId,
    String mode,
    String firstPlayer,
    String secondPlayer,
    String firstSecretNumber,
    String secondSecretNumber,
    String currentTurn,
    String winner,
    String status) {
}
