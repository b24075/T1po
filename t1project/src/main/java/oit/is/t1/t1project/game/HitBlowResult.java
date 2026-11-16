package oit.is.t1.t1project.game;

public record HitBlowResult(int hits, int blows) {

  public boolean isWin() {
    return hits == 3;
  }
}