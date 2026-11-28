package oit.is.t1.t1project.match;

import oit.is.t1.t1project.game.MatchState;
import oit.is.t1.t1project.result.MatchResult;

public class MatchRecord {

  private String matchId;
  private String mode;
  private String firstPlayer;
  private String secondPlayer;
  private String firstSecretNumber;
  private String secondSecretNumber;
  private String currentTurn;
  private String winner;
  private String status;

  public MatchRecord() {
  }

  public static MatchRecord from(MatchState match) {
    var snapshot = match.snapshot();
    MatchRecord record = new MatchRecord();
    record.setMatchId(snapshot.matchId());
    record.setMode(snapshot.mode());
    record.setFirstPlayer(snapshot.firstPlayer());
    record.setSecondPlayer(snapshot.secondPlayer());
    record.setFirstSecretNumber(snapshot.firstSecretNumber());
    record.setSecondSecretNumber(snapshot.secondSecretNumber());
    record.setCurrentTurn(snapshot.currentTurn());
    record.setWinner(snapshot.winner());
    record.setStatus(snapshot.status());
    return record;
  }

  public MatchState toMatchState() {
    return new MatchState(matchId, mode, firstPlayer, secondPlayer,
        firstSecretNumber, secondSecretNumber, currentTurn, winner);
  }

  public MatchResult toMatchResult() {
    return new MatchResult(matchId, mode, firstPlayer, secondPlayer, winner);
  }

  public String getMatchId() {
    return matchId;
  }

  public void setMatchId(String matchId) {
    this.matchId = matchId;
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public String getFirstPlayer() {
    return firstPlayer;
  }

  public void setFirstPlayer(String firstPlayer) {
    this.firstPlayer = firstPlayer;
  }

  public String getSecondPlayer() {
    return secondPlayer;
  }

  public void setSecondPlayer(String secondPlayer) {
    this.secondPlayer = secondPlayer;
  }

  public String getFirstSecretNumber() {
    return firstSecretNumber;
  }

  public void setFirstSecretNumber(String firstSecretNumber) {
    this.firstSecretNumber = firstSecretNumber;
  }

  public String getSecondSecretNumber() {
    return secondSecretNumber;
  }

  public void setSecondSecretNumber(String secondSecretNumber) {
    this.secondSecretNumber = secondSecretNumber;
  }

  public String getCurrentTurn() {
    return currentTurn;
  }

  public void setCurrentTurn(String currentTurn) {
    this.currentTurn = currentTurn;
  }

  public String getWinner() {
    return winner;
  }

  public void setWinner(String winner) {
    this.winner = winner;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
