package oit.is.t1.t1project.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HitBlowServiceTest {

  private final HitBlowService service = new HitBlowService();

  @Test
  void 相手の秘密の数字に対するHitとBlowを判定してターンを交代する() {
    MatchState match = readyGame();

    HitBlowResult result = service.submitGuess(match, "player1", "465");

    assertEquals(1, result.hits());
    assertEquals(2, result.blows());
    assertEquals(result, match.getLastResult());
    assertEquals(1, match.getGuessHistory().size());
    assertEquals("player1", match.getGuessHistory().get(0).playerName());
    assertEquals("465", match.getGuessHistory().get(0).guess());
    assertFalse(match.isCurrentTurn("player1"));
    assertTrue(match.isCurrentTurn("player2"));
  }

  @Test
  void ターンではないプレイヤーの予想は受け付けない() {
    MatchState match = readyGame();

    assertThrows(IllegalStateException.class, () -> service.submitGuess(match, "player2", "456"));
  }

  @Test
  void 重複または3桁でない予想は受け付けない() {
    MatchState match = readyGame();

    assertThrows(IllegalArgumentException.class, () -> service.submitGuess(match, "player1", "112"));
    assertThrows(IllegalArgumentException.class, () -> service.submitGuess(match, "player1", "12"));
  }

  @Test
  void 三Hitで試合終了し勝者が確定する() {
    MatchState match = readyGame();

    HitBlowResult result = service.submitGuess(match, "player1", "456");

    assertTrue(result.isWin());
    assertTrue(match.isFinished());
    assertEquals("player1", match.getWinner());
    assertThrows(IllegalStateException.class, () -> service.submitGuess(match, "player2", "123"));
  }

  @Test
  void 毎回の予想を入力順に履歴として保持する() {
    MatchState match = readyGame();

    service.submitGuess(match, "player1", "465");
    service.submitGuess(match, "player2", "123");

    assertEquals(2, match.getGuessHistory().size());
    assertEquals("player1", match.getGuessHistory().get(0).playerName());
    assertEquals("player2", match.getGuessHistory().get(1).playerName());
    assertEquals("123", match.getGuessHistory().get(1).guess());
    assertEquals(3, match.getGuessHistory().get(1).result().hits());
  }

  @Test
  void Cpu戦ではCpuの予想後にプレイヤーへターンが戻る() {
    MatchState match = new MatchState(
        "cpu-test", MatchState.PLAYER_VS_CPU, "player1", MatchState.CPU_PLAYER);
    match.setSecretNumber("player1", "123");
    match.setSecretNumber(MatchState.CPU_PLAYER, "456");

    service.submitGuess(match, "player1", "789");

    assertEquals(2, match.getGuessHistory().size());
    assertEquals(MatchState.CPU_PLAYER, match.getGuessHistory().get(1).playerName());
    assertTrue(match.isCurrentTurn("player1") || match.isFinished());
  }

  private MatchState readyGame() {
    MatchState match = new MatchState(
        "human-test", MatchState.PLAYER_VS_PLAYER, "player1", "player2");
    match.setSecretNumber("player1", "123");
    match.setSecretNumber("player2", "456");
    return match;
  }
}
