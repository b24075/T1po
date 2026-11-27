package oit.is.t1.t1project.match;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MatchControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void モード選択に人対人と人対CPUの開始ボタンがある() throws Exception {
    MockHttpSession session = loginAs("player1");

    mockMvc.perform(get("/mode").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("/matches/human")))
        .andExpect(content().string(containsString("/matches/cpu")));
  }

  @Test
  void 人対人を選ぶと試合ID付きの対戦画面へ遷移する() throws Exception {
    MockHttpSession session = loginAs("player1");

    MvcResult result = mockMvc.perform(post("/matches/human").with(csrf()).session(session))
        .andExpect(status().is3xxRedirection())
        .andReturn();
    String location = result.getResponse().getRedirectedUrl();

    org.junit.jupiter.api.Assertions.assertTrue(location.matches("/game\\?matchId=[a-f0-9-]+"));
    mockMvc.perform(get(location).session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("試合ID:")));
  }

  @Test
  void 人対CPUを選ぶと待機画面を経ずに対戦画面へ遷移する() throws Exception {
    MockHttpSession session = loginAs("player2");

    MvcResult result = mockMvc.perform(post("/matches/cpu").with(csrf()).session(session))
        .andExpect(status().is3xxRedirection())
        .andReturn();
    String location = result.getResponse().getRedirectedUrl();

    org.junit.jupiter.api.Assertions.assertTrue(location.matches("/game\\?matchId=[a-f0-9-]+"));
    mockMvc.perform(get(location).session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("対戦")))
        .andExpect(content().string(notContains("対戦相手を待っています。")));
  }

  private MockHttpSession loginAs(String username) throws Exception {
    MvcResult loginResult = mockMvc.perform(formLogin("/login")
        .user(username)
        .password("password"))
        .andReturn();
    return (MockHttpSession) loginResult.getRequest().getSession(false);
  }

  private org.hamcrest.Matcher<String> notContains(String value) {
    return org.hamcrest.Matchers.not(containsString(value));
  }
}
