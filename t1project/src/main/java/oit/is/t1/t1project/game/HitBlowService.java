package oit.is.t1.t1project.game;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class HitBlowService {

  private static final String FIRST_PLAYER = "player1";
  private static final String SECOND_PLAYER = "player2";

  private final GameSetupService gameSetupService;
  private final Map<String, String> secretNumbers = new ConcurrentHashMap<>();
  private final List<GuessRecord> guessHistory = new ArrayList<>();
  private String currentTurn = FIRST_PLAYER;
  private String winner;
  private HitBlowResult lastResult;

  public HitBlowService(GameSetupService gameSetupService) {
    this.gameSetupService = gameSetupService;
  }

  public HitBlowResult submitGuess(String playerName, String guess) {
    validateGuess(guess);
    if (!gameSetupService.bothPlayersReady(FIRST_PLAYER, SECOND_PLAYER)) {
      throw new IllegalStateException("両者が秘密の数字を決定するまで予想できません。");
    }
    synchronized (this) {
      if (winner != null) {
        throw new IllegalStateException("この試合は終了しています。");
      }
      if (!playerName.equals(currentTurn)) {
        throw new IllegalStateException("現在はあなたのターンではありません。");
      }

      String opponent = opponentOf(playerName);
      HitBlowResult result = judge(guess, secretNumberOf(opponent));
      guessHistory.add(new GuessRecord(playerName, guess, result));
      lastResult = result;
      if (result.isWin()) {
        winner = playerName;
      } else {
        currentTurn = opponent;
      }
      return result;
    }
  }

  public boolean isCurrentTurn(String playerName) {
    synchronized (this) {
      return winner == null && currentTurn.equals(playerName);
    }
  }

  public boolean isFinished() {
    synchronized (this) {
      return winner != null;
    }
  }

  public String getWinner() {
    synchronized (this) {
      return winner;
    }
  }

  public HitBlowResult getLastResult() {
    synchronized (this) {
      return lastResult;
    }
  }

  public List<GuessRecord> getGuessHistory() {
    synchronized (this) {
      return List.copyOf(guessHistory);
    }
  }

  private String secretNumberOf(String playerName) {
    return secretNumbers.computeIfAbsent(playerName, gameSetupService::getSecretNumber);
  }

  private String opponentOf(String playerName) {
    if (FIRST_PLAYER.equals(playerName)) {
      return SECOND_PLAYER;
    }
    if (SECOND_PLAYER.equals(playerName)) {
      return FIRST_PLAYER;
    }
    throw new IllegalArgumentException("対戦に参加していないプレイヤーです。");
  }

  private HitBlowResult judge(String guess, String secret) {
    int hits = 0;
    int blows = 0;
    for (int index = 0; index < guess.length(); index++) {
      if (guess.charAt(index) == secret.charAt(index)) {
        hits++;
      } else if (secret.indexOf(guess.charAt(index)) >= 0) {
        blows++;
      }
    }
    return new HitBlowResult(hits, blows);
  }

  private void validateGuess(String guess) {
    if (guess == null || !guess.matches("[0-9]{3}")
        || guess.chars().distinct().count() != 3) {
      throw new IllegalArgumentException("予想は重複のない3桁の数字で入力してください。");
    }
  }
}