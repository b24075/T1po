package oit.is.t1.t1project.result;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ResultController {

  private final ResultService resultService;

  public ResultController(ResultService resultService) {
    this.resultService = resultService;
  }

  @GetMapping("/results")
  public String results(Authentication authentication, Model model) {
    model.addAttribute("results", resultService.findByPlayer(authentication.getName()));
    return "results";
  }

  @GetMapping({"/result", "/results/detail"})
  public String result(
      @RequestParam("matchId") String matchId,
      Authentication authentication,
      Model model) {
    MatchResult result = resultService.findByMatchId(matchId);
    if (result == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "試合結果が見つかりません。");
    }
    if (!resultService.belongsTo(result, authentication.getName())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "この試合結果を閲覧する権限がありません。");
    }

    model.addAttribute("result", result);
    return "result";
  }
}