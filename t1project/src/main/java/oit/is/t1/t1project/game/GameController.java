package oit.is.t1.t1project.game;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    String playerName = authentication.getName();
    MatchState match = matchService.findMatch(matchId);

    if (!match.hasPlayer(playerName)) {
      throw new IllegalStateException(
          "この試合の参加者ではありません。");
    }

    model.addAttribute("matchId", matchId);
    model.addAttribute("playerName", playerName);
    model.addAttribute(
        "secretNumberSet",
        gameSetupService.hasSecretNumber(playerName));
    model.addAttribute(
        "bothPlayersReady",
        gameSetupService.bothPlayersReady(
            match.getFirstPlayer(),
            match.getSecondPlayer()));
    model.addAttribute(
        "isCurrentTurn",
        hitBlowService.isCurrentTurn(
            matchId,
            playerName));
    model.addAttribute(
        "gameFinished",
        hitBlowService.isFinished(matchId));
    model.addAttribute(
        "winner",
        hitBlowService.getWinner(matchId));
    model.addAttribute(
        "lastResult",
        hitBlowService.getLastResult(matchId));
    model.addAttribute(
        "guessHistory",
        hitBlowService.getGuessHistory(matchId));

    return "game";
  }

  @PostMapping("/game/secret")
  public String setSecretNumber(
      @RequestParam("matchId") String matchId,
      Authentication authentication,
      @RequestParam("secretNumber") String value,
      RedirectAttributes redirectAttributes) {

    try {
      MatchState match = matchService.findMatch(matchId);

      if (!match.hasPlayer(authentication.getName())) {
        throw new IllegalStateException(
            "この試合の参加者ではありません。");
      }

      gameSetupService.setSecretNumber(
          authentication.getName(),
          value);

      redirectAttributes.addFlashAttribute(
          "secretNumberMessage",
          "秘密の数字を決定しました。");
    } catch (IllegalArgumentException
        | IllegalStateException exception) {
      redirectAttributes.addFlashAttribute(
          "secretNumberError",
          exception.getMessage());
    }

    return "redirect:/game?matchId=" + matchId;
  }

  @PostMapping("/game/guess")
  public String submitGuess(
      @RequestParam("matchId") String matchId,
      Authentication authentication,
      @RequestParam("guess") String guess,
      RedirectAttributes redirectAttributes) {

    try {
      HitBlowResult result = hitBlowService.submitGuess(
          matchId,
          authentication.getName(),
          guess);

      redirectAttributes.addFlashAttribute(
          "guessMessage",
          "判定結果: "
              + result.hits()
              + " Hit / "
              + result.blows()
              + " Blow");
    } catch (IllegalArgumentException
        | IllegalStateException exception) {
      redirectAttributes.addFlashAttribute(
          "guessError",
          exception.getMessage());
    }

    return "redirect:/game?matchId=" + matchId;
  }
}
