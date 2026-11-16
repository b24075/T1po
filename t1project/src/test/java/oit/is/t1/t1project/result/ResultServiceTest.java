package oit.is.t1.t1project.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

  @Mock
  private MatchResultMapper matchResultMapper;

  @InjectMocks
  private ResultService resultService;

  @Test
  void 同じ試合IDの結果を二重保存しない() {
    MatchResult existingResult = new MatchResult(
        "match-duplicate",
        "PLAYER_VS_PLAYER",
        "player1",
        "player2",
        "player1",
        java.time.LocalDateTime.now());

    org.mockito.Mockito.when(
        matchResultMapper.findByMatchId("match-duplicate"))
        .thenReturn(existingResult);

    resultService.saveResult(
        "match-duplicate",
        "PLAYER_VS_PLAYER",
        "player1",
        "player2",
        "player1");

    org.mockito.Mockito.verify(
        matchResultMapper,
        org.mockito.Mockito.never())
        .insert(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void 結果の参加者を判定できる() {
    MatchResult result = new MatchResult(
        "match-participant",
        "PLAYER_VS_PLAYER",
        "player1",
        "player2",
        "player1",
        java.time.LocalDateTime.now());

    assertTrue(
        resultService.belongsTo(result, "player1"));

    assertTrue(
        resultService.belongsTo(result, "player2"));

    assertFalse(
        resultService.belongsTo(result, "unknown"));
  }
}
