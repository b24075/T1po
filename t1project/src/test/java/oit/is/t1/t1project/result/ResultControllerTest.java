package oit.is.t1.t1project.result;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import oit.is.t1.t1project.match.MatchMapper;
import oit.is.t1.t1project.match.MatchRecord;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ResultControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MatchMapper matchMapper;

  @Test
  void 人対人の結果画面に対戦者と勝者を表示する() throws Exception {
    saveResult("human-match", "PLAYER_VS_PLAYER", "player1", "player2", "player2");

    MockHttpSession session = loginAs("player2");

    mockMvc.perform(get("/result").param("matchId", "human-match").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("player1")))
        .andExpect(content().string(containsString("player2")))
        .andExpect(content().string(containsString("モード選択に戻る")))
        .andExpect(content().string(containsString("/mode")));
  }

  @Test
  void 人対CPUの結果画面にCPUと勝者を表示する() throws Exception {
    saveResult("cpu-match", "PLAYER_VS_CPU", "player1", "CPU", "player1");

    MockHttpSession session = loginAs("player1");

    mockMvc.perform(get("/result").param("matchId", "cpu-match").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("player1")))
        .andExpect(content().string(containsString("CPU")));
  }

  @Test
  void モード選択ページから過去の試合結果へのリンクを表示する() throws Exception {
    MockHttpSession session = loginAs("player1");

    mockMvc.perform(get("/mode").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("過去の試合結果")))
        .andExpect(content().string(containsString("/results")));
  }

  @Test
  void 過去の試合結果にはログイン中のプレイヤーが参加した結果だけ表示する() throws Exception {
    saveResult("player-result", "PLAYER_VS_PLAYER", "player1", "player2", "player1");
    saveResult("other-result", "PLAYER_VS_PLAYER", "player2", "other", "other");

    MockHttpSession session = loginAs("player1");

    mockMvc.perform(get("/results").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("player-result")))
        .andExpect(content().string(not(containsString("other-result"))));
  }

  @Test
  void 別の試合の結果を表示しない() throws Exception {
    saveResult("match-one", "PLAYER_VS_PLAYER", "player1", "player2", "player1");
    saveResult("match-two", "PLAYER_VS_PLAYER", "player1", "player2", "player2");

    MockHttpSession session = loginAs("player1");

    mockMvc.perform(get("/result").param("matchId", "match-one").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("match-one")))
        .andExpect(content().string(not(containsString("match-two"))));
  }

  @Test
  void 参加していない試合の結果は表示できない() throws Exception {
    saveResult("private-match", "PLAYER_VS_CPU", "player1", "CPU", "player1");

    MockHttpSession session = loginAs("player2");

    mockMvc.perform(get("/result").param("matchId", "private-match").session(session))
        .andExpect(status().isForbidden());
  }

  private MockHttpSession loginAs(String username) throws Exception {
    MvcResult loginResult = mockMvc.perform(formLogin("/login")
        .user(username)
        .password("password"))
        .andReturn();
    return (MockHttpSession) loginResult.getRequest().getSession(false);
  }

  private void saveResult(
      String matchId, String mode, String firstPlayer, String secondPlayer, String winner) {
    MatchRecord match = new MatchRecord();
    match.setMatchId(matchId);
    match.setMode(mode);
    match.setFirstPlayer(firstPlayer);
    match.setSecondPlayer(secondPlayer);
    match.setCurrentTurn(winner);
    match.setWinner(winner);
    match.setStatus("FINISHED");
    matchMapper.insert(match);
  }
}
