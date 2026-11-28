package oit.is.t1.t1project.match;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import oit.is.t1.t1project.game.MatchState;

@Service
public class MatchService {

  private final Map<String, MatchState> matches = new ConcurrentHashMap<>();
  private final MatchMapper matchMapper;

  public MatchService(MatchMapper matchMapper) {
    this.matchMapper = matchMapper;
  }

  public synchronized MatchState findOrCreateHumanMatch(String playerName) {
    MatchState waitingForPlayer = null;
    for (MatchRecord record : matchMapper.findWaitingHumanMatches()) {
      MatchState match = findOrRestore(record);
      if (match.isWaitingForOpponent()) {
        if (!match.getFirstPlayer().equals(playerName)) {
          match.join(playerName);
          saveState(match);
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
    matchMapper.insert(MatchRecord.from(match));
    matches.put(match.getMatchId(), match);
    return match;
  }

  public MatchState createCpuMatch(String playerName) {
    MatchState match = new MatchState(
        UUID.randomUUID().toString(), MatchState.PLAYER_VS_CPU, playerName, MatchState.CPU_PLAYER);
    match.setSecretNumber(MatchState.CPU_PLAYER, createCpuSecretNumber());
    matchMapper.insert(MatchRecord.from(match));
    matches.put(match.getMatchId(), match);
    return match;
  }

  public MatchState findById(String matchId) {
    MatchState cached = matches.get(matchId);
    if (cached != null) {
      return cached;
    }
    MatchRecord record = matchMapper.findById(matchId);
    return record == null ? null : findOrRestore(record);
  }

  public void saveState(MatchState match) {
    if (matchMapper.update(MatchRecord.from(match)) == 0) {
      throw new IllegalStateException("保存対象の試合が見つかりません。");
    }
    matches.put(match.getMatchId(), match);
  }

  private MatchState findOrRestore(MatchRecord record) {
    return matches.computeIfAbsent(record.getMatchId(), ignored -> record.toMatchState());
  }

  private String createCpuSecretNumber() {
    String value;
    do {
      value = String.format(Locale.ROOT, "%03d", ThreadLocalRandom.current().nextInt(1000));
    } while (value.chars().distinct().count() != 3);
    return value;
  }
}
