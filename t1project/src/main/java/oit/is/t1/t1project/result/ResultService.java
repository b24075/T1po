package oit.is.t1.t1project.result;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class ResultService {

  private final Map<String, MatchResult> results = new ConcurrentHashMap<>();

  public void save(MatchResult result) {
    results.putIfAbsent(result.matchId(), result);
  }

  public MatchResult findByMatchId(String matchId) {
    return results.get(matchId);
  }

  public boolean belongsTo(MatchResult result, String playerName) {
    return result.firstPlayer().equals(playerName)
        || result.secondPlayer().equals(playerName);
  }
}