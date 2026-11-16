package oit.is.t1.t1project.game;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class GameController {

  private static final String FIRST_PLAYER = "player1";
  private static final String SECOND_PLAYER = "player2";

  private final GameSetupService gameSetupService;
  private final HitBlowService hitBlowService;

  public GameController(GameSetupService gameSetupService, HitBlowService hitBlowService) {
    this.gameSetupService = gameSetupService;
    this.hitBlowService = hitBlowService;
  }

  @GetMapping("/game")
  public String game(Authentication authentication, Model model) {
    String playerName = authentication.getName();
    model.addAttribute("playerName", playerName);
    model.addAttribute("secretNumberSet", gameSetupService.hasSecretNumber(playerName));
    model.addAttribute("bothPlayersReady", gameSetupService.bothPlayersReady(FIRST_PLAYER, SECOND_PLAYER));
    model.addAttribute("isCurrentTurn", hitBlowService.isCurrentTurn(playerName));
    model.addAttribute("gameFinished", hitBlowService.isFinished());
    model.addAttribute("winner", hitBlowService.getWinner());
    model.addAttribute("lastResult", hitBlowService.getLastResult());
    model.addAttribute("guessHistory", hitBlowService.getGuessHistory());
    return "game";
  }

  @PostMapping("/game/secret")
  public String setSecretNumber(Authentication authentication,
      @RequestParam("secretNumber") String value, RedirectAttributes redirectAttributes) {
    try {
      gameSetupService.setSecretNumber(authentication.getName(), value);
      redirectAttributes.addFlashAttribute("secretNumberMessage", "秘密の数字を決定しました。");
    } catch (IllegalArgumentException exception) {
      redirectAttributes.addFlashAttribute("secretNumberError", exception.getMessage());
    }
    return "redirect:/game";
  }

  @PostMapping("/game/guess")
  public String submitGuess(Authentication authentication,
      @RequestParam("guess") String guess, RedirectAttributes redirectAttributes) {
    try {
      HitBlowResult result = hitBlowService.submitGuess(authentication.getName(), guess);
      redirectAttributes.addFlashAttribute("guessMessage",
          "判定結果: " + result.hits() + " Hit / " + result.blows() + " Blow");
    } catch (IllegalArgumentException | IllegalStateException exception) {
      redirectAttributes.addFlashAttribute("guessError", exception.getMessage());
    }
    return "redirect:/game";
  }
}