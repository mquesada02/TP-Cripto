package ar.edu.itba.cys.utils;

import java.util.Random;

public class RandomGenerator {
  public static final int URIS_FAVORITE_NUMBER = 43;
  private static int seed;
  private static Random random;

  public static void setSeed(int selectedSeed) {
    random = new Random(seed);
    seed = selectedSeed;
  }

  public static Random getRandom() {
    return random;
  }
  public static int getSeed() {
    return seed;
  }

}
