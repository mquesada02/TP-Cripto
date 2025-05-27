package ar.edu.itba.cys.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class LagrangianInterpolation implements ModularInterpolation {

  private static int mod(int a) {
    int mod = RemainderTable.PRIME_MOD;
    return ((a % mod) + mod) % mod;
  }

  private static int modAdd(int a, int b) {
    int mod = RemainderTable.PRIME_MOD;
    return ((a + b) % mod + mod) % mod;
  }

  private static int modMul(int a, int b) {
    int mod = RemainderTable.PRIME_MOD;
    return ((a % mod) * (b % mod) + mod) % mod;
  }

  private static int[] polyMul(int[] a, int[] b) {
    int[] res = new int[a.length + b.length - 1];
    for (int i = 0; i < a.length; i++) {
      for (int j = 0; j < b.length; j++) {
        res[i + j] = modAdd(res[i + j], modMul(a[i], b[j]));
      }
    }
    return res;
  }

  public static List<Integer> getCoefficients(List<Integer> ys) {
    int r = ys.size();
    int[] xs = IntStream.rangeClosed(1, r).toArray();
    List<Integer> coefficients = new ArrayList<>(Collections.nCopies(r, 0));
    for (int j = 0; j < r; j++) {
      int[] basis = new int[]{1};
      int denom = 1;
      for (int m = 0; m < r; m++) {
        if (m == j) continue;
        int x_m = xs[m];
        int diff = mod(xs[j] - x_m);
        denom = modMul(denom, diff);
        int mod = RemainderTable.PRIME_MOD;
        basis = polyMul(basis, new int[]{(mod - x_m) % mod, 1});
      }
      int scalar = modMul(ys.get(j), RemainderTable.getMultiplicativeInverse(denom));
      for (int i = 0; i < basis.length; i++) {
        basis[i] = modMul(basis[i], scalar);
      }
      for (int i = 0; i < basis.length; i++) {
        coefficients.set(i, modAdd(coefficients.get(i), basis[i]));
      }
    }
    return coefficients;
  }

}
