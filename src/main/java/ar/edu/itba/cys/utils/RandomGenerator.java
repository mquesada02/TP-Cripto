package ar.edu.itba.cys.utils;

import java.util.Random;

public class RandomGenerator {
  private static final int URIS_FAVORITE_NUMBER = 43;
  private static Random random;

  public static Random getRandom() {
    if (random == null) {
      random = new Random(URIS_FAVORITE_NUMBER);
    }
    return random;
  }

}
