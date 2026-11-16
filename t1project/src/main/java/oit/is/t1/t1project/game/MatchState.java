package oit.is.t1.t1project.game;

import java.util.ArrayList;
import java.util.List;

public class MatchState {

  private final String matchId;
  private final String firstPlayer;
  private final String secondPlayer;

  private String currentTurn;
  private String winner;
  private HitBlowResult lastResult;
  private final List<GuessRecord> guessHistory = new ArrayList<>();

  public MatchState(
      String matchId,
      String firstPlayer,
      String secondPlayer) {
    this.matchId = matchId;
    this.firstPlayer = firstPlayer;
    this.secondPlayer = secondPlayer;
    this.currentTurn = firstPlayer;
  }

  public String getMatchId() {
    return matchId;
  }

  public String getFirstPlayer() {
    return firstPlayer;
  }

  public String getSecondPlayer() {
    return secondPlayer;
  }

  public String getCurrentTurn() {
    return currentTurn;
  }

  public void changeTurn() {
    currentTurn = currentTurn.equals(firstPlayer)
        ? secondPlayer
        : firstPlayer;
  }

  public String getWinner() {
    return winner;
  }

  public void setWinner(String winner) {
    this.winner = winner;
  }

  public HitBlowResult getLastResult() {
    return lastResult;
  }

  public void setLastResult(HitBlowResult lastResult) {
    this.lastResult = lastResult;
  }

  public void addGuessRecord(GuessRecord record) {
    guessHistory.add(record);
  }

  public List<GuessRecord> getGuessHistory() {
    return List.copyOf(guessHistory);
  }

  public boolean isFinished() {
    return winner != null;
  }

  public boolean hasPlayer(String playerName) {
    return firstPlayer.equals(playerName)
        || secondPlayer.equals(playerName);
  }
}
