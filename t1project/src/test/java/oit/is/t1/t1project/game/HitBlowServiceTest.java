package oit.is.t1.t1project.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HitBlowServiceTest {

  @Test
  void 相手の秘密の数字に対するHitとBlowを判定してターンを交代する() {
    HitBlowService service = new HitBlowService(readyGame());

    HitBlowResult result = service.submitGuess("player1", "465");

    assertEquals(1, result.hits());
    assertEquals(2, result.blows());
    assertEquals(result, service.getLastResult());
    assertEquals(1, service.getGuessHistory().size());
    assertEquals("player1", service.getGuessHistory().get(0).playerName());
    assertEquals("465", service.getGuessHistory().get(0).guess());
    assertFalse(service.isCurrentTurn("player1"));
    assertTrue(service.isCurrentTurn("player2"));
  }

  @Test
  void ターンではないプレイヤーの予想は受け付けない() {
    HitBlowService service = new HitBlowService(readyGame());

    assertThrows(IllegalStateException.class, () -> service.submitGuess("player2", "456"));
  }

  @Test
  void 重複または3桁でない予想は受け付けない() {
    HitBlowService service = new HitBlowService(readyGame());

    assertThrows(IllegalArgumentException.class, () -> service.submitGuess("player1", "112"));
    assertThrows(IllegalArgumentException.class, () -> service.submitGuess("player1", "12"));
  }

  @Test
  void 三Hitで試合終了し勝者が確定する() {
    HitBlowService service = new HitBlowService(readyGame());

    HitBlowResult result = service.submitGuess("player1", "456");

    assertTrue(result.isWin());
    assertTrue(service.isFinished());
    assertEquals("player1", service.getWinner());
    assertThrows(IllegalStateException.class, () -> service.submitGuess("player2", "123"));
  }

  @Test
  void 毎回の予想を入力順に履歴として保持する() {
    HitBlowService service = new HitBlowService(readyGame());

    service.submitGuess("player1", "465");
    service.submitGuess("player2", "123");

    assertEquals(2, service.getGuessHistory().size());
    assertEquals("player1", service.getGuessHistory().get(0).playerName());
    assertEquals("player2", service.getGuessHistory().get(1).playerName());
    assertEquals("123", service.getGuessHistory().get(1).guess());
    assertEquals(3, service.getGuessHistory().get(1).result().hits());
  }

  private GameSetupService readyGame() {
    GameSetupService setupService = new GameSetupService();
    setupService.setSecretNumber("player1", "123");
    setupService.setSecretNumber("player2", "456");
    return setupService;
  }
}