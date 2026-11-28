package oit.is.t1.t1project.game;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import oit.is.t1.t1project.match.MatchService;

@Controller
public class GameController {

  private final GameSetupService gameSetupService;
  private final HitBlowService hitBlowService;
  private final MatchService matchService;

  public GameController(
      GameSetupService gameSetupService,
      HitBlowService hitBlowService,
      MatchService matchService) {
    this.gameSetupService = gameSetupService;
    this.hitBlowService = hitBlowService;
    this.matchService = matchService;
  }

  @GetMapping("/game")
  public String game(
      @RequestParam("matchId") String matchId,
      Authentication authentication,
      Model model) {
    MatchState match = requireMatch(matchId, authentication.getName());
    String playerName = authentication.getName();
    model.addAttribute("matchId", matchId);
    model.addAttribute("playerName", playerName);
    model.addAttribute("secretNumberSet", gameSetupService.hasSecretNumber(match, playerName));
    model.addAttribute("bothPlayersReady", gameSetupService.bothPlayersReady(match));
    model.addAttribute("waitingForOpponent", match.isWaitingForOpponent());
    model.addAttribute("isCurrentTurn", match.isCurrentTurn(playerName));
    model.addAttribute("gameFinished", match.isFinished());
    model.addAttribute("winner", match.getWinner());
    model.addAttribute("lastResult", match.getLastResult());
    model.addAttribute("guessHistory", match.getGuessHistory());
    return "game";
  }

  @PostMapping("/game/secret")
  public String setSecretNumber(
      @RequestParam("matchId") String matchId,
      Authentication authentication,
      @RequestParam("secretNumber") String value,
      RedirectAttributes redirectAttributes) {
    MatchState match = requireMatch(matchId, authentication.getName());
    try {
      gameSetupService.setSecretNumber(match, authentication.getName(), value);
      matchService.saveState(match);
      redirectAttributes.addFlashAttribute("secretNumberMessage", "秘密の数字を決定しました。");
    } catch (IllegalArgumentException | IllegalStateException exception) {
      redirectAttributes.addFlashAttribute("secretNumberError", exception.getMessage());
    }
    return redirectToGame(matchId);
  }

  @PostMapping("/game/guess")
  public String submitGuess(
      @RequestParam("matchId") String matchId,
      Authentication authentication,
      @RequestParam("guess") String guess,
      RedirectAttributes redirectAttributes) {
    MatchState match = requireMatch(matchId, authentication.getName());
    try {
      HitBlowResult result = hitBlowService.submitGuess(match, authentication.getName(), guess);
      matchService.saveState(match);
      redirectAttributes.addFlashAttribute("guessMessage",
          "判定結果: " + result.hits() + " Hit / " + result.blows() + " Blow");
    } catch (IllegalArgumentException | IllegalStateException exception) {
      redirectAttributes.addFlashAttribute("guessError", exception.getMessage());
    }
    return redirectToGame(matchId);
  }

  private MatchState requireMatch(String matchId, String playerName) {
    MatchState match = matchService.findById(matchId);
    if (match == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "試合が見つかりません。");
    }
    if (!match.isParticipant(playerName)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "この試合に参加する権限がありません。");
    }
    return match;
  }

  private String redirectToGame(String matchId) {
    return "redirect:/game?matchId=" + matchId;
  }
}
