package oit.is.t1.t1project.match;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import oit.is.t1.t1project.game.GameSetupService;
import oit.is.t1.t1project.game.HitBlowService;
import oit.is.t1.t1project.game.MatchState;
import oit.is.t1.t1project.result.MatchResult;
import oit.is.t1.t1project.result.ResultService;

@MybatisTest
@Import(MatchService.class)
class MatchServiceTest {

  @Autowired
  private MatchService service;

  @Autowired
  private MatchMapper matchMapper;

  @Test
  void 人対人の二人目は同じ待機試合へ参加する() {
    MatchState first = service.findOrCreateHumanMatch("player-a");
    MatchState second = service.findOrCreateHumanMatch("player-b");

    assertEquals(first.getMatchId(), second.getMatchId());
    assertEquals("player-b", first.getSecondPlayer());
    assertFalse(first.isWaitingForOpponent());
  }

  @Test
  void 人対CPUではCPU参加者を持つ別個の試合を作成する() {
    MatchState first = service.createCpuMatch("player-a");
    MatchState second = service.createCpuMatch("player-a");

    assertNotEquals(first.getMatchId(), second.getMatchId());
    assertEquals(MatchState.PLAYER_VS_CPU, first.getMode());
    assertEquals(MatchState.CPU_PLAYER, first.getSecondPlayer());
    assertTrue(first.hasSecretNumber(MatchState.CPU_PLAYER));
    assertFalse(first.isWaitingForOpponent());
  }

  @Test
  void 参加者ターンと予想履歴は試合IDごとに分離される() {
    GameSetupService setupService = new GameSetupService();
    HitBlowService hitBlowService = new HitBlowService();

    MatchState first = service.findOrCreateHumanMatch("first-a");
    service.findOrCreateHumanMatch("first-b");
    MatchState second = service.findOrCreateHumanMatch("second-a");
    service.findOrCreateHumanMatch("second-b");
    setupService.setSecretNumber(first, "first-a", "123");
    setupService.setSecretNumber(first, "first-b", "456");
    setupService.setSecretNumber(second, "second-a", "789");
    setupService.setSecretNumber(second, "second-b", "012");

    hitBlowService.submitGuess(first, "first-a", "465");

    assertEquals(1, first.getGuessHistory().size());
    assertTrue(first.isCurrentTurn("first-b"));
    assertTrue(second.getGuessHistory().isEmpty());
    assertTrue(second.isCurrentTurn("second-a"));
    assertFalse(second.isFinished());
  }

  @Test
  void 試合状態と終了結果をDBから復元する() {
    GameSetupService setupService = new GameSetupService();
    HitBlowService hitBlowService = new HitBlowService();
    MatchState match = service.findOrCreateHumanMatch("restore-a");
    service.findOrCreateHumanMatch("restore-b");
    setupService.setSecretNumber(match, "restore-a", "123");
    setupService.setSecretNumber(match, "restore-b", "456");
    hitBlowService.submitGuess(match, "restore-a", "465");
    service.saveState(match);

    MatchState restored = new MatchService(matchMapper).findById(match.getMatchId());
    assertEquals("restore-b", restored.getSecondPlayer());
    assertEquals("restore-b", restored.getCurrentTurn());
    assertEquals("IN_PROGRESS", restored.getStatus());
    assertTrue(restored.hasSecretNumber("restore-a"));
    assertTrue(restored.hasSecretNumber("restore-b"));

    hitBlowService.submitGuess(restored, "restore-b", "123");
    new MatchService(matchMapper).saveState(restored);
    MatchResult result = new ResultService(matchMapper).findByMatchId(match.getMatchId());

    assertEquals("restore-b", result.winner());
    assertEquals("restore-a", result.firstPlayer());
    assertEquals("restore-b", result.secondPlayer());
  }
}
