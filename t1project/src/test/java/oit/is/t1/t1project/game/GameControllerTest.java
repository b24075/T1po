package oit.is.t1.t1project.game;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import oit.is.t1.t1project.match.MatchService;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MatchService matchService;

  @Autowired
  private GameSetupService gameSetupService;

  @Test
  void 決定済みの秘密の数字は対戦画面に表示されない() throws Exception {
    MatchState match = matchService.createCpuMatch("player2");
    gameSetupService.setSecretNumber(match, "player2", "123");
    MockHttpSession session = loginAs("player2");

    mockMvc.perform(get("/game").param("matchId", match.getMatchId()).session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(not(containsString("123"))));
  }

  @Test
  void 対戦画面にログイン中のプレイヤー名が表示される() throws Exception {
    MatchState match = matchService.createCpuMatch("player2");
    MockHttpSession session = loginAs("player2");

    mockMvc.perform(get("/game").param("matchId", match.getMatchId()).session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("プレイヤー:")))
        .andExpect(content().string(containsString("player2")));
  }

  @Test
  void 参加していない試合の対戦画面は表示できない() throws Exception {
    MatchState match = matchService.createCpuMatch("player1");
    MockHttpSession session = loginAs("player2");

    mockMvc.perform(get("/game").param("matchId", match.getMatchId()).session(session))
        .andExpect(status().isForbidden());
  }

  @Test
  void Cpu戦終了後は結果画面からモード選択へ戻れる() throws Exception {
    MatchState match = matchService.createCpuMatch("player2");
    gameSetupService.setSecretNumber(match, "player2", "123");
    String cpuSecretNumber = match.secretNumberOf(MatchState.CPU_PLAYER);
    MockHttpSession session = loginAs("player2");

    mockMvc.perform(post("/game/guess")
        .with(csrf())
        .session(session)
        .param("matchId", match.getMatchId())
        .param("guess", cpuSecretNumber))
        .andExpect(status().is3xxRedirection());

    mockMvc.perform(get("/result")
        .session(session)
        .param("matchId", match.getMatchId()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("player2")))
        .andExpect(content().string(containsString("CPU")))
        .andExpect(content().string(containsString("/mode")));
  }

  private MockHttpSession loginAs(String username) throws Exception {
    MvcResult loginResult = mockMvc.perform(formLogin("/login")
        .user(username)
        .password("password"))
        .andReturn();
    return (MockHttpSession) loginResult.getRequest().getSession(false);
  }
}
