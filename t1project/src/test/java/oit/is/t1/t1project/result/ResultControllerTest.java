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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class ResultControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ResultService resultService;

  @Test
  void 人対人の結果画面に対戦者と勝者を表示する() throws Exception {
    resultService.save(new MatchResult("human-match", "PLAYER_VS_PLAYER", "player1", "player2", "player2"));

    MockHttpSession session = loginAs("player2");

    mockMvc.perform(get("/result").param("matchId", "human-match").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("player1")))
        .andExpect(content().string(containsString("player2")));
  }

  @Test
  void 人対CPUの結果画面にCPUと勝者を表示する() throws Exception {
    resultService.save(new MatchResult("cpu-match", "PLAYER_VS_CPU", "player1", "CPU", "player1"));

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
          resultService.save(new MatchResult("player-result", "PLAYER_VS_PLAYER", "player1", "player2", "player1"));
          resultService.save(new MatchResult("other-result", "PLAYER_VS_PLAYER", "player2", "other", "other"));

          MockHttpSession session = loginAs("player1");

          mockMvc.perform(get("/results").session(session))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("player-result")))
          .andExpect(content().string(not(containsString("other-result"))));
        }
  @Test
  void 別の試合の結果を表示しない() throws Exception {
    resultService.save(new MatchResult("match-one", "PLAYER_VS_PLAYER", "player1", "player2", "player1"));
    resultService.save(new MatchResult("match-two", "PLAYER_VS_PLAYER", "player1", "player2", "player2"));

    MockHttpSession session = loginAs("player1");

    mockMvc.perform(get("/result").param("matchId", "match-one").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("match-one")))
        .andExpect(content().string(not(containsString("match-two"))));
  }

  @Test
  void 参加していない試合の結果は表示できない() throws Exception {
    resultService.save(new MatchResult("private-match", "PLAYER_VS_CPU", "player1", "CPU", "player1"));

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
}