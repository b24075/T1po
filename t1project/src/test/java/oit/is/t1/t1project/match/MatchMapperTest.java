package oit.is.t1.t1project.match;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

@MybatisTest
class MatchMapperTest {

  @Autowired
  private MatchMapper matchMapper;

  @Test
  void 試合情報を保存更新して取得する() {
    MatchRecord match = match(
        "mapper-match", "PLAYER_VS_PLAYER", "player-a", "player-b",
        "123", "456", "player-a", null, "IN_PROGRESS");
    matchMapper.insert(match);

    MatchRecord stored = matchMapper.findById("mapper-match");
    assertEquals("player-a", stored.getFirstPlayer());
    assertEquals("player-b", stored.getSecondPlayer());
    assertEquals("123", stored.getFirstSecretNumber());
    assertEquals("456", stored.getSecondSecretNumber());
    assertEquals("player-a", stored.getCurrentTurn());
    assertEquals("IN_PROGRESS", stored.getStatus());

    stored.setCurrentTurn("player-b");
    stored.setWinner("player-b");
    stored.setStatus("FINISHED");
    assertEquals(1, matchMapper.update(stored));

    MatchRecord finished = matchMapper.findById("mapper-match");
    assertEquals("player-b", finished.getCurrentTurn());
    assertEquals("player-b", finished.getWinner());
    assertEquals("FINISHED", finished.getStatus());
  }

  @Test
  void 参加者が未確定の試合と結果一覧を条件に応じて検索する() {
    matchMapper.insert(match(
        "waiting-match", "PLAYER_VS_PLAYER", "waiting-player", null,
        null, null, "waiting-player", null, "WAITING"));
    matchMapper.insert(match(
        "finished-match", "PLAYER_VS_CPU", "result-player", "CPU",
        "123", "456", "result-player", "result-player", "FINISHED"));

    assertEquals(1, matchMapper.findWaitingHumanMatches().size());
    assertEquals("waiting-match", matchMapper.findWaitingHumanMatches().get(0).getMatchId());
    assertEquals("finished-match", matchMapper.findByPlayer("result-player").get(0).getMatchId());
    assertNull(matchMapper.findById("missing-match"));
  }

  @Test
  void 複数試合の秘密の数字ターン状態と勝者を試合IDごとに分離する() {
    matchMapper.insert(match(
        "isolated-one", "PLAYER_VS_PLAYER", "one-a", "one-b",
        "123", "456", "one-b", null, "IN_PROGRESS"));
    matchMapper.insert(match(
        "isolated-two", "PLAYER_VS_CPU", "two-a", "CPU",
        "789", "012", "two-a", "two-a", "FINISHED"));

    MatchRecord first = matchMapper.findById("isolated-one");
    MatchRecord second = matchMapper.findById("isolated-two");

    assertEquals("456", first.getSecondSecretNumber());
    assertEquals("one-b", first.getCurrentTurn());
    assertNull(first.getWinner());
    assertEquals("012", second.getSecondSecretNumber());
    assertEquals("two-a", second.getCurrentTurn());
    assertEquals("two-a", second.getWinner());
  }

  private MatchRecord match(
      String matchId,
      String mode,
      String firstPlayer,
      String secondPlayer,
      String firstSecretNumber,
      String secondSecretNumber,
      String currentTurn,
      String winner,
      String status) {
    MatchRecord match = new MatchRecord();
    match.setMatchId(matchId);
    match.setMode(mode);
    match.setFirstPlayer(firstPlayer);
    match.setSecondPlayer(secondPlayer);
    match.setFirstSecretNumber(firstSecretNumber);
    match.setSecondSecretNumber(secondSecretNumber);
    match.setCurrentTurn(currentTurn);
    match.setWinner(winner);
    match.setStatus(status);
    return match;
  }
}
