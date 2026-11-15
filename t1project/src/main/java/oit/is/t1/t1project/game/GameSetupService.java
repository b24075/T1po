package oit.is.t1.t1project.game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class GameSetupService {

  private final Map<String, SecretNumber> secretNumbers = new ConcurrentHashMap<>();

  public void setSecretNumber(String playerName, String value) {
    secretNumbers.put(playerName, new SecretNumber(value));
  }

  public boolean hasSecretNumber(String playerName) {
    return secretNumbers.containsKey(playerName);
  }

  public boolean bothPlayersReady(String firstPlayer, String secondPlayer) {
    return hasSecretNumber(firstPlayer) && hasSecretNumber(secondPlayer);
  }
}