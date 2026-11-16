package oit.is.t1.t1project.game;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
  private GameSetupService gameSetupService;

  @Autowired
  private MatchService matchService;

  @Test
  void 相手の秘密の数字は対戦画面に表示されない() throws Exception {
    gameSetupService.setSecretNumber("player1", "123");
    MatchState match = matchService.createMatch("player1", "player2");
    MvcResult loginResult = mockMvc.perform(formLogin("/login")
        .user("player2")
        .password("password"))
        .andReturn();
    MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

    mockMvc.perform(get("/game")
      .param("matchId", match.getMatchId())
      .session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(not(containsString("123"))));
  }

  @Test
  void 対戦画面にログイン中のプレイヤー名が表示される() throws Exception {
    MvcResult loginResult = mockMvc.perform(formLogin("/login")
        .user("player2")
        .password("password"))
        .andReturn();
    MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
    MatchState match = matchService.createMatch("player1", "player2");

    mockMvc.perform(get("/game")
      .param("matchId", match.getMatchId())
      .session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("プレイヤー:")))
        .andExpect(content().string(containsString("player2")));
  }
}