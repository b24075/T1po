package oit.is.t1.t1project.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oit.is.t1.t1project.match.MatchService;
import oit.is.t1.t1project.result.MatchResultMapper;
import oit.is.t1.t1project.result.ResultService;

@ExtendWith(MockitoExtension.class)
class HitBlowServiceTest {

  @Mock
  private MatchResultMapper matchResultMapper;

  private GameSetupService gameSetupService;
  private MatchService matchService;
  private HitBlowService service;
  private MatchState match;

  @BeforeEach
  void setUp() {
    gameSetupService = new GameSetupService();
    gameSetupService.setSecretNumber("player1", "123");
    gameSetupService.setSecretNumber("player2", "456");
    matchService = new MatchService();
    match = matchService.createMatch("player1", "player2");
    service = new HitBlowService(
        gameSetupService,
        matchService,
        new ResultService(matchResultMapper));
  }

  @Test
  void 試合IDごとにHitとBlowを判定してターンを交代する() {
    HitBlowResult result = service.submitGuess(
        match.getMatchId(), "player1", "465");

    assertEquals(1, result.hits());
    assertEquals(2, result.blows());
    assertEquals(result,
        service.getLastResult(match.getMatchId()));
    assertFalse(service.isCurrentTurn(
        match.getMatchId(), "player1"));
    assertTrue(service.isCurrentTurn(
        match.getMatchId(), "player2"));
  }

  @Test
  void ターンではないプレイヤーの予想は受け付けない() {
    assertThrows(IllegalStateException.class, () -> service.submitGuess(
        match.getMatchId(), "player2", "456"));
  }

  @Test
  void 重複または3桁でない予想は受け付けない() {
    assertThrows(IllegalArgumentException.class, () -> service.submitGuess(
        match.getMatchId(), "player1", "112"));
    assertThrows(IllegalArgumentException.class, () -> service.submitGuess(
        match.getMatchId(), "player1", "12"));
  }

  @Test
  void 三Hitで試合終了し勝者が確定する() {
    HitBlowResult result = service.submitGuess(
        match.getMatchId(), "player1", "456");

    assertTrue(result.isWin());
    assertTrue(service.isFinished(match.getMatchId()));
    assertEquals("player1",
        service.getWinner(match.getMatchId()));
  }

  @Test
  void 複数試合の状態が混ざらない() {
    MatchState secondMatch =
        matchService.createMatch("player1", "player2");

    service.submitGuess(
        match.getMatchId(), "player1", "465");

    assertTrue(service.isCurrentTurn(
        secondMatch.getMatchId(), "player1"));
    assertFalse(service.isFinished(
        secondMatch.getMatchId()));
  }
}
