package oit.is.t1.t1project.result;

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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:result-test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ResultControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ResultService resultService;

  @Test
  void ログイン中のプレイヤーが参加した試合結果だけ表示される()
      throws Exception {

    resultService.saveResult(
        "match-player1",
        "PLAYER_VS_PLAYER",
        "player1",
        "player2",
        "player1");

    resultService.saveResult(
        "match-player2",
        "PLAYER_VS_CPU",
        "player2",
        "CPU",
        "CPU");

    MvcResult loginResult = mockMvc.perform(
        formLogin("/login")
            .user("player1")
            .password("password"))
        .andReturn();

    MockHttpSession session =
        (MockHttpSession) loginResult
            .getRequest()
            .getSession(false);

    mockMvc.perform(get("/results").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("player1")))
        .andExpect(content().string(containsString("player2")))
        .andExpect(content().string(not(containsString("CPU"))));
  }

  @Test
  void 人対人の対戦者名と勝者が表示される()
      throws Exception {

    resultService.saveResult(
        "match-human",
        "PLAYER_VS_PLAYER",
        "player1",
        "player2",
        "player2");

    MvcResult loginResult = mockMvc.perform(
        formLogin("/login")
            .user("player2")
            .password("password"))
        .andReturn();

    MockHttpSession session =
        (MockHttpSession) loginResult
            .getRequest()
            .getSession(false);

    mockMvc.perform(get("/results").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("player1")))
        .andExpect(content().string(containsString("player2")));
  }

  @Test
  void 人対CPUのCPUと勝者が表示される()
      throws Exception {

    resultService.saveResult(
        "match-cpu",
        "PLAYER_VS_CPU",
        "player1",
        "CPU",
        "CPU");

    MvcResult loginResult = mockMvc.perform(
        formLogin("/login")
            .user("player1")
            .password("password"))
        .andReturn();

    MockHttpSession session =
        (MockHttpSession) loginResult
            .getRequest()
            .getSession(false);

    mockMvc.perform(get("/results").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("CPU")))
        .andExpect(content().string(containsString("player1")));
  }

  @Test
  void 未ログインユーザーは結果画面を閲覧できない()
      throws Exception {

    mockMvc.perform(get("/results"))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void 結果画面にHit数Blow数予想履歴を表示しない()
      throws Exception {

    resultService.saveResult(
        "match-display-only",
        "PLAYER_VS_PLAYER",
        "player1",
        "player2",
        "player1");

    MvcResult loginResult = mockMvc.perform(
        formLogin("/login")
            .user("player1")
            .password("password"))
        .andReturn();

    MockHttpSession session =
        (MockHttpSession) loginResult
            .getRequest()
            .getSession(false);

    mockMvc.perform(get("/results").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(not(containsString("Hit"))))
        .andExpect(content().string(not(containsString("Blow"))))
        .andExpect(content().string(not(containsString("予想履歴"))));
  }
}
