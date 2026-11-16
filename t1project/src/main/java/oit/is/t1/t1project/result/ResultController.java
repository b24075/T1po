package oit.is.t1.t1project.result;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResultController {

  private final ResultService resultService;

  public ResultController(ResultService resultService) {
    this.resultService = resultService;
  }

  @GetMapping("/results")
  public String results(
      Authentication authentication,
      Model model) {

    String playerName = authentication.getName();

    List<MatchResult> results =
        resultService.findByPlayer(playerName);

    model.addAttribute("results", results);

    return "results";
  }

  @GetMapping("/results/detail")
  public String resultDetail(
      @RequestParam("matchId") String matchId,
      Authentication authentication,
      Model model) {

    String playerName = authentication.getName();

    MatchResult result =
        resultService.findByMatchId(matchId);

    if (!resultService.belongsTo(result, playerName)) {
      throw new IllegalStateException(
          "この試合結果を閲覧する権限がありません。");
    }

    model.addAttribute("result", result);

    return "result";
  }
}
