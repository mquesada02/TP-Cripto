package ar.edu.itba.cys.math;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class LagrangianInterpolation {

  public static int interpolate(int x, List<Integer> coefficients) {
    int acum = 0;
    int xAcum = 1;
    for (int coefficient : coefficients) {
      acum = (acum + (coefficient * xAcum) % RemainderTable.PRIME_MOD) % RemainderTable.PRIME_MOD;
      xAcum = (xAcum * x) % RemainderTable.PRIME_MOD;
    }
    return acum % RemainderTable.PRIME_MOD;
  }

  public static List<Integer> getCoefficients(List<Integer> ys) {
    int n = ys.size();
    List<Integer> xs = IntStream.rangeClosed(1, n).boxed().toList();
    int[] result = new int[n];

    for (int j = 0; j < n; j++) {
      int[] basis = {1};
      int denom = 1;

      for (int m = 0; m < n; m++) {
        if (m == j) continue;
        basis = polyMul(basis, new int[]{mod(-xs.get(m)), 1});
        denom = modMul(denom, mod(xs.get(j) - xs.get(m)));
      }

      int scalar = modMul(ys.get(j), modInverse(denom));
      for (int i = 0; i < basis.length; i++) {
        basis[i] = modMul(basis[i], scalar);
      }

      result = polyAdd(result, basis);
    }

    return toList(result);
  }

  public static int[] polyMul(int[] a, int[] b) {
    int[] res = new int[a.length + b.length - 1];
    for (int i = 0; i < a.length; i++) {
      for (int j = 0; j < b.length; j++) {
        res[i + j] = modAdd(res[i + j], modMul(a[i], b[j]));
      }
    }
    return res;
  }

  public static int[] polyAdd(int[] a, int[] b) {
    int[] res = new int[Math.max(a.length, b.length)];
    for (int i = 0; i < res.length; i++) {
      int ai = i < a.length ? a[i] : 0;
      int bi = i < b.length ? b[i] : 0;
      res[i] = modAdd(ai, bi);
    }
    return res;
  }

  public static int modAdd(int a, int b) {
    return (a + b) % RemainderTable.PRIME_MOD;
  }

  public static int modMul(int a, int b) {
    return (int)(((long)a * b) % RemainderTable.PRIME_MOD);
  }

  public static int modInverse(int a) {
    return RemainderTable.getMultiplicativeInverse(a);
  }

  public static int mod(int x) {
    x %= RemainderTable.PRIME_MOD;
    return x < 0 ? x + RemainderTable.PRIME_MOD : x;
  }

  public static List<Integer> toList(int[] arr) {
    List<Integer> list = new ArrayList<>(arr.length);
    for (int x : arr) list.add(x);
    return list;
  }

}
