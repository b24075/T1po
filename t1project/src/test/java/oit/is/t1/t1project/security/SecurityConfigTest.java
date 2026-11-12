package oit.is.t1.t1project.security;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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
class SecurityConfigTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void 有効なアカウントでログインするとモード選択画面に名前が表示される() throws Exception {
    MvcResult loginResult = mockMvc.perform(formLogin("/login")
        .user("player1")
        .password("password"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/mode"))
        .andReturn();

    MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
    mockMvc.perform(get("/mode").session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("player1")));
  }

  @Test
  void 無効なアカウントではログインに失敗してモード選択画面に遷移しない() throws Exception {
    mockMvc.perform(formLogin("/login")
        .user("unknown")
        .password("wrong-password"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login?error=true"));

    mockMvc.perform(get("/login?error=true"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("ユーザー名またはパスワードが正しくありません。")));
  }
}
