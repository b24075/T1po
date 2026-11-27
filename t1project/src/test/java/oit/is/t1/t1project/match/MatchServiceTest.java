package oit.is.t1.t1project.match;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import oit.is.t1.t1project.game.GameSetupService;
import oit.is.t1.t1project.game.HitBlowService;
import oit.is.t1.t1project.game.MatchState;
import oit.is.t1.t1project.result.ResultService;

class MatchServiceTest {

  @Test
  void 人対人の二人目は同じ待機試合へ参加する() {
    MatchService service = new MatchService(new ResultService());

    MatchState first = service.findOrCreateHumanMatch("player-a");
    MatchState second = service.findOrCreateHumanMatch("player-b");

    assertEquals(first.getMatchId(), second.getMatchId());
    assertEquals("player-b", first.getSecondPlayer());
    assertFalse(first.isWaitingForOpponent());
  }

  @Test
  void 人対CPUではCPU参加者を持つ別個の試合を作成する() {
    MatchService service = new MatchService(new ResultService());

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
    MatchService service = new MatchService(new ResultService());
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
}
