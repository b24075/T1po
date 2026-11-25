package oit.is.t1.t1project.game;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MatchState {

  public static final String PLAYER_VS_PLAYER = "PLAYER_VS_PLAYER";
  public static final String PLAYER_VS_CPU = "PLAYER_VS_CPU";
  public static final String CPU_PLAYER = "CPU";

  private final String matchId;
  private final String mode;
  private final String firstPlayer;
  private String secondPlayer;
  private final Map<String, SecretNumber> secretNumbers = new HashMap<>();
  private final List<GuessRecord> guessHistory = new ArrayList<>();
  private String currentTurn;
  private String winner;
  private HitBlowResult lastResult;

  public MatchState(String matchId, String mode, String firstPlayer, String secondPlayer) {
    this.matchId = matchId;
    this.mode = mode;
    this.firstPlayer = firstPlayer;
    this.secondPlayer = secondPlayer;
    this.currentTurn = firstPlayer;
  }

  public String getMatchId() {
    return matchId;
  }

  public String getMode() {
    return mode;
  }

  public String getFirstPlayer() {
    return firstPlayer;
  }

  public synchronized String getSecondPlayer() {
    return secondPlayer;
  }

  public synchronized boolean isWaitingForOpponent() {
    return secondPlayer == null;
  }

  public synchronized void join(String playerName) {
    if (secondPlayer != null || firstPlayer.equals(playerName)) {
      throw new IllegalStateException("この試合には参加できません。");
    }
    secondPlayer = playerName;
  }

  public synchronized boolean isParticipant(String playerName) {
    return firstPlayer.equals(playerName) || playerName.equals(secondPlayer);
  }

  public synchronized void setSecretNumber(String playerName, String value) {
    requireParticipant(playerName);
    if (secretNumbers.containsKey(playerName)) {
      throw new IllegalStateException("秘密の数字はすでに決定されています。");
    }
    secretNumbers.put(playerName, new SecretNumber(value));
  }

  public synchronized boolean hasSecretNumber(String playerName) {
    return secretNumbers.containsKey(playerName);
  }

  public synchronized boolean bothPlayersReady() {
    return secondPlayer != null
        && secretNumbers.containsKey(firstPlayer)
        && secretNumbers.containsKey(secondPlayer);
  }

  public synchronized boolean isCurrentTurn(String playerName) {
    return winner == null && currentTurn.equals(playerName);
  }

  public synchronized String getCurrentTurn() {
    return currentTurn;
  }

  public synchronized boolean isFinished() {
    return winner != null;
  }

  public synchronized String getWinner() {
    return winner;
  }

  public synchronized HitBlowResult getLastResult() {
    return lastResult;
  }

  public synchronized List<GuessRecord> getGuessHistory() {
    return List.copyOf(guessHistory);
  }

  synchronized void assertCanGuess(String playerName) {
    requireParticipant(playerName);
    if (!bothPlayersReady()) {
      throw new IllegalStateException("両者が秘密の数字を決定するまで予想できません。");
    }
    if (winner != null) {
      throw new IllegalStateException("この試合は終了しています。");
    }
    if (!currentTurn.equals(playerName)) {
      throw new IllegalStateException("現在はあなたのターンではありません。");
    }
  }

  synchronized String secretNumberOf(String playerName) {
    SecretNumber secretNumber = secretNumbers.get(playerName);
    if (secretNumber == null) {
      throw new IllegalStateException("秘密の数字が決定されていません。");
    }
    return secretNumber.value();
  }

  synchronized String opponentOf(String playerName) {
    requireParticipant(playerName);
    if (firstPlayer.equals(playerName)) {
      return secondPlayer;
    }
    return firstPlayer;
  }

  synchronized void recordGuess(String playerName, String guess, HitBlowResult result) {
    guessHistory.add(new GuessRecord(playerName, guess, result));
    lastResult = result;
    if (result.isWin()) {
      winner = playerName;
    } else {
      currentTurn = opponentOf(playerName);
    }
  }

  private void requireParticipant(String playerName) {
    if (!isParticipant(playerName)) {
      throw new IllegalArgumentException("対戦に参加していないプレイヤーです。");
    }
  }
}
