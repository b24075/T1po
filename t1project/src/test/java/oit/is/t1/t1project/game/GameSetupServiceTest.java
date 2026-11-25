package oit.is.t1.t1project.game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameSetupServiceTest {

  private final GameSetupService service = new GameSetupService();
  private final MatchState match = new MatchState(
      "setup-test", MatchState.PLAYER_VS_PLAYER, "player1", "player2");

  @Test
  void 重複のない3桁の秘密の数字をプレイヤーごとに保存できる() {
    service.setSecretNumber(match, "player1", "123");
    service.setSecretNumber(match, "player2", "456");

    assertTrue(service.hasSecretNumber(match, "player1"));
    assertTrue(service.hasSecretNumber(match, "player2"));
    assertTrue(service.bothPlayersReady(match));
  }

  @Test
  void 桁数不足や桁数超過や重複した数字は保存できない() {
    assertThrows(IllegalArgumentException.class, () -> service.setSecretNumber(match, "player1", "12"));
    assertThrows(IllegalArgumentException.class, () -> service.setSecretNumber(match, "player1", "1234"));
    assertThrows(IllegalArgumentException.class, () -> service.setSecretNumber(match, "player1", "112"));
    assertFalse(service.hasSecretNumber(match, "player1"));
  }

  @Test
  void 片方だけ決定した場合は予想入力へ進めない() {
    service.setSecretNumber(match, "player1", "123");

    assertFalse(service.bothPlayersReady(match));
  }
}
