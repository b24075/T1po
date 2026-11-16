package oit.is.t1.t1project.match;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import oit.is.t1.t1project.game.MatchState;

@Service
public class MatchService {

  private final Map<String, MatchState> matches =
      new ConcurrentHashMap<>();

  public MatchState createMatch(
      String firstPlayer,
      String secondPlayer) {

    String matchId = UUID.randomUUID().toString();

    MatchState match = new MatchState(
        matchId,
        firstPlayer,
        secondPlayer);

    matches.put(matchId, match);
    return match;
  }

  public MatchState findMatch(String matchId) {
    MatchState match = matches.get(matchId);

    if (match == null) {
      throw new IllegalArgumentException("試合が見つかりません。");
    }

    return match;
  }
}
