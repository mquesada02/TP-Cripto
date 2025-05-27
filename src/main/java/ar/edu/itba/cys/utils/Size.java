package ar.edu.itba.cys.utils;

public class Size extends Pair<Integer,Integer> {
  public Size(int width, int height) {
    super(width, height);
  }

  public int getWidth() {
    return getFirst();
  }

  public int getHeight() {
    return getSecond();
  }
}
