package oit.is.t1.t1project.match;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import oit.is.t1.t1project.game.MatchState;

@Controller
public class MatchController {

  private final MatchService matchService;

  public MatchController(MatchService matchService) {
    this.matchService = matchService;
  }

  @PostMapping("/matches/human")
  public String startHumanMatch(Authentication authentication) {
    MatchState match = matchService.findOrCreateHumanMatch(authentication.getName());
    return redirectToGame(match);
  }

  @PostMapping("/matches/cpu")
  public String startCpuMatch(Authentication authentication) {
    MatchState match = matchService.createCpuMatch(authentication.getName());
    return redirectToGame(match);
  }

  private String redirectToGame(MatchState match) {
    return "redirect:/game?matchId=" + match.getMatchId();
  }
}
