package oit.is.t1.t1project.game;

import java.util.HashSet;

public record SecretNumber(String value) {

  public SecretNumber {
    if (value == null || !value.matches("[0-9]{3}")
        || new HashSet<>(value.chars().boxed().toList()).size() != 3) {
      throw new IllegalArgumentException("秘密の数字は重複しない3桁の数字で入力してください。");
    }
  }
}