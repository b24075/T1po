package oit.is.t1.t1project.game;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import oit.is.t1.t1project.match.MatchService;
import oit.is.t1.t1project.result.ResultService;

@Service
public class HitBlowService {

  private final GameSetupService gameSetupService;
  private final MatchService matchService;
  private final ResultService resultService;

  private final Map<String, Map<String, String>> secretNumbers =
      new ConcurrentHashMap<>();

  public HitBlowService(
      GameSetupService gameSetupService,
      MatchService matchService,
      ResultService resultService) {
    this.gameSetupService = gameSetupService;
    this.matchService = matchService;
    this.resultService = resultService;
  }

  public HitBlowResult submitGuess(
      String matchId,
      String playerName,
      String guess) {

    validateGuess(guess);

    MatchState match = matchService.findMatch(matchId);

    if (!match.hasPlayer(playerName)) {
      throw new IllegalStateException(
          "この試合の参加者ではありません。");
    }

    synchronized (match) {
      if (match.isFinished()) {
        throw new IllegalStateException(
            "この試合は終了しています。");
      }

      if (!playerName.equals(match.getCurrentTurn())) {
        throw new IllegalStateException(
            "現在はあなたのターンではありません。");
      }

      String opponent = match.getFirstPlayer().equals(playerName)
          ? match.getSecondPlayer()
          : match.getFirstPlayer();

      HitBlowResult result = judge(
          guess,
          secretNumberOf(matchId, opponent));

      match.addGuessRecord(
          new GuessRecord(playerName, guess, result));
      match.setLastResult(result);

      if (result.isWin()) {
        match.setWinner(playerName);

        resultService.saveResult(
            match.getMatchId(),
            "PLAYER_VS_PLAYER",
            match.getFirstPlayer(),
            match.getSecondPlayer(),
            playerName);
      } else {
        match.changeTurn();
      }

      return result;
    }
  }

  public boolean isCurrentTurn(
      String matchId,
      String playerName) {

    MatchState match = matchService.findMatch(matchId);

    synchronized (match) {
      return !match.isFinished()
          && match.getCurrentTurn().equals(playerName);
    }
  }

  public boolean isFinished(String matchId) {
    return matchService.findMatch(matchId).isFinished();
  }

  public String getWinner(String matchId) {
    return matchService.findMatch(matchId).getWinner();
  }

  public HitBlowResult getLastResult(String matchId) {
    return matchService.findMatch(matchId).getLastResult();
  }

  public List<GuessRecord> getGuessHistory(String matchId) {
    return matchService.findMatch(matchId).getGuessHistory();
  }

  private String secretNumberOf(
      String matchId,
      String playerName) {

    Map<String, String> matchSecrets =
        secretNumbers.computeIfAbsent(
            matchId,
            ignored -> new ConcurrentHashMap<>());

    return matchSecrets.computeIfAbsent(
        playerName,
        gameSetupService::getSecretNumber);
  }

  private HitBlowResult judge(
      String guess,
      String secret) {

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
    if (guess == null
        || !guess.matches("[0-9]{3}")
        || guess.chars().distinct().count() != 3) {
      throw new IllegalArgumentException(
          "予想は重複のない3桁の数字で入力してください。");
    }
  }
}
