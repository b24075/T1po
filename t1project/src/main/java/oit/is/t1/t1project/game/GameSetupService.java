package oit.is.t1.t1project.game;

import org.springframework.stereotype.Service;

@Service
public class GameSetupService {

  public void setSecretNumber(MatchState match, String playerName, String value) {
    match.setSecretNumber(playerName, value);
  }

  public boolean hasSecretNumber(MatchState match, String playerName) {
    return match.hasSecretNumber(playerName);
  }

  public boolean bothPlayersReady(MatchState match) {
    return match.bothPlayersReady();
  }
}
