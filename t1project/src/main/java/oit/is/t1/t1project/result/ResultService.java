package oit.is.t1.t1project.result;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ResultService {

  private final MatchResultMapper matchResultMapper;

  public ResultService(MatchResultMapper matchResultMapper) {
    this.matchResultMapper = matchResultMapper;
  }

  public synchronized void saveResult(
      String matchId,
      String mode,
      String firstPlayer,
      String secondPlayer,
      String winner) {

    MatchResult existingResult =
        matchResultMapper.findByMatchId(matchId);

    if (existingResult != null) {
      return;
    }

    MatchResult result = new MatchResult(
        matchId,
        mode,
        firstPlayer,
        secondPlayer,
        winner,
        LocalDateTime.now());

    matchResultMapper.insert(result);
  }

  public List<MatchResult> findByPlayer(String playerName) {
    return matchResultMapper.findByPlayer(playerName);
  }

  public MatchResult findByMatchId(String matchId) {
    MatchResult result =
        matchResultMapper.findByMatchId(matchId);

    if (result == null) {
      throw new IllegalArgumentException(
          "試合結果が見つかりません。");
    }

    return result;
  }

  public boolean belongsTo(
      MatchResult result,
      String playerName) {

    return result.firstPlayer().equals(playerName)
        || result.secondPlayer().equals(playerName);
  }
}
