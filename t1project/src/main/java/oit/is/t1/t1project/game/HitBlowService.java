package oit.is.t1.t1project.game;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

@Service
public class HitBlowService {

  public HitBlowResult submitGuess(MatchState match, String playerName, String guess) {
    validateGuess(guess);
    synchronized (match) {
      match.assertCanGuess(playerName);
      HitBlowResult result = judge(guess, match.secretNumberOf(match.opponentOf(playerName)));
      match.recordGuess(playerName, guess, result);
      if (MatchState.PLAYER_VS_CPU.equals(match.getMode())
          && !match.isFinished()
          && MatchState.CPU_PLAYER.equals(match.getCurrentTurn())) {
        submitCpuGuess(match);
      }
      return result;
    }
  }

  private void submitCpuGuess(MatchState match) {
    String guess;
    do {
      guess = String.format(Locale.ROOT, "%03d", ThreadLocalRandom.current().nextInt(1000));
    } while (guess.chars().distinct().count() != 3);

    HitBlowResult result = judge(guess, match.secretNumberOf(match.getFirstPlayer()));
    match.recordGuess(MatchState.CPU_PLAYER, guess, result);
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
