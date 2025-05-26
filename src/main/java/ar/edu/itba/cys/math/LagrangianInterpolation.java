package ar.edu.itba.cys.math;

import java.util.function.Function;
import java.util.stream.IntStream;

public class LagrangianInterpolation implements ModularInterpolation {
  private Function<Integer, Integer> interpolator;

  @Override
  public void initialize(int ...ys) {
    int r = ys.length;
    int[] xs = IntStream.rangeClosed(1, r).toArray();
    this.interpolator = x -> {
      int l = 0;
      for (int j = 0; j < r; j++) {
        int lj = 1;
        for (int m = 0; m < r; m++) {
          if (m == j) continue;
          lj *= (x - xs[m]) * RemainderTable.getMultiplicativeInverse(xs[j] - xs[m]);
        }
        l += ys[j] * lj;
      }
      return l % RemainderTable.PRIME_MOD;
    };
  }

  @Override
  public int interpolate(int x) {
    return interpolator.apply(x);
  }
}
