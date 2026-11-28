package oit.is.t1.t1project.result;

import java.util.List;

import org.springframework.stereotype.Service;

import oit.is.t1.t1project.match.MatchMapper;
import oit.is.t1.t1project.match.MatchRecord;

@Service
public class ResultService {

  private final MatchMapper matchMapper;

  public ResultService(MatchMapper matchMapper) {
    this.matchMapper = matchMapper;
  }

  public MatchResult findByMatchId(String matchId) {
    MatchRecord match = matchMapper.findById(matchId);
    return match == null || match.getWinner() == null ? null : match.toMatchResult();
  }

  public List<MatchResult> findByPlayer(String playerName) {
    return matchMapper.findByPlayer(playerName).stream()
        .map(MatchRecord::toMatchResult)
        .toList();
  }

  public boolean belongsTo(MatchResult result, String playerName) {
    return result.firstPlayer().equals(playerName)
        || result.secondPlayer().equals(playerName);
  }
}