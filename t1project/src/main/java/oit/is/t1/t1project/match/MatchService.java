package oit.is.t1.t1project.match;

import java.util.Map;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import oit.is.t1.t1project.game.MatchState;
import oit.is.t1.t1project.result.MatchResult;
import oit.is.t1.t1project.result.ResultService;

@Service
public class MatchService {

  private final Map<String, MatchState> matches = new ConcurrentHashMap<>();
  private final ResultService resultService;

  public MatchService(ResultService resultService) {
    this.resultService = resultService;
  }

  public synchronized MatchState findOrCreateHumanMatch(String playerName) {
    MatchState waitingForPlayer = null;
    for (MatchState match : matches.values()) {
      if (match.isWaitingForOpponent()) {
        if (!match.getFirstPlayer().equals(playerName)) {
          match.join(playerName);
          return match;
        }
        waitingForPlayer = match;
      }
    }
    if (waitingForPlayer != null) {
      return waitingForPlayer;
    }

    MatchState match = new MatchState(
        UUID.randomUUID().toString(), MatchState.PLAYER_VS_PLAYER, playerName, null);
    matches.put(match.getMatchId(), match);
    return match;
  }

  public MatchState createCpuMatch(String playerName) {
    MatchState match = new MatchState(
        UUID.randomUUID().toString(), MatchState.PLAYER_VS_CPU, playerName, MatchState.CPU_PLAYER);
    match.setSecretNumber(MatchState.CPU_PLAYER, createCpuSecretNumber());
    matches.put(match.getMatchId(), match);
    return match;
  }

  public MatchState findById(String matchId) {
    return matches.get(matchId);
  }

  public void saveResultIfFinished(MatchState match) {
    if (match.isFinished()) {
      resultService.save(new MatchResult(
          match.getMatchId(), match.getMode(), match.getFirstPlayer(),
          match.getSecondPlayer(), match.getWinner()));
    }
  }

  private String createCpuSecretNumber() {
    String value;
    do {
      value = String.format(Locale.ROOT, "%03d", ThreadLocalRandom.current().nextInt(1000));
    } while (value.chars().distinct().count() != 3);
    return value;
  }
}
