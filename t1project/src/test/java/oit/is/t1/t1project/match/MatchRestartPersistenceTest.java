package oit.is.t1.t1project.match;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import oit.is.t1.t1project.T1projectApplication;
import oit.is.t1.t1project.game.GameSetupService;
import oit.is.t1.t1project.game.HitBlowService;
import oit.is.t1.t1project.game.MatchState;
import oit.is.t1.t1project.result.MatchResult;
import oit.is.t1.t1project.result.ResultService;

class MatchRestartPersistenceTest {

  @Test
  void アプリケーション再起動後も試合結果を取得できる(@TempDir Path temporaryDirectory) {
    String databasePath = temporaryDirectory.resolve("restart-test")
        .toString()
        .replace('\\', '/');
    String datasourceUrl = "jdbc:h2:file:" + databasePath;
    String matchId;

    try (ConfigurableApplicationContext firstApplication = startApplication(datasourceUrl)) {
      MatchService matchService = firstApplication.getBean(MatchService.class);
      GameSetupService gameSetupService = firstApplication.getBean(GameSetupService.class);
      HitBlowService hitBlowService = firstApplication.getBean(HitBlowService.class);
      MatchState match = matchService.findOrCreateHumanMatch("restart-a");
      matchService.findOrCreateHumanMatch("restart-b");
      gameSetupService.setSecretNumber(match, "restart-a", "123");
      gameSetupService.setSecretNumber(match, "restart-b", "456");
      hitBlowService.submitGuess(match, "restart-a", "465");
      matchService.saveState(match);
      hitBlowService.submitGuess(match, "restart-b", "123");
      matchService.saveState(match);
      matchId = match.getMatchId();
    }

    try (ConfigurableApplicationContext restartedApplication = startApplication(datasourceUrl)) {
      ResultService resultService = restartedApplication.getBean(ResultService.class);
      MatchResult result = resultService.findByMatchId(matchId);

      assertNotNull(result);
      assertEquals("restart-a", result.firstPlayer());
      assertEquals("restart-b", result.secondPlayer());
      assertEquals("restart-b", result.winner());
    }
  }

  private ConfigurableApplicationContext startApplication(String datasourceUrl) {
    return new SpringApplicationBuilder(T1projectApplication.class)
        .web(WebApplicationType.NONE)
        .run("--spring.datasource.url=" + datasourceUrl,
            "--spring.main.banner-mode=off");
  }
}
