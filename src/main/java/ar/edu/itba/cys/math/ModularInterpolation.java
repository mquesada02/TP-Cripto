package ar.edu.itba.cys.math;

import ar.edu.itba.cys.exception.MethodNotImplementedException;

import java.util.List;

public interface ModularInterpolation {

  static List<Integer> getCoefficients(List<Integer> ys) {
    throw new MethodNotImplementedException();
  }

  static int interpolate(int x, List<Integer> coefficients) {
    int acum = 0;
    int xAcum = 1;
    for (int coefficient : coefficients) {
      acum = (acum + (coefficient * xAcum) % RemainderTable.PRIME_MOD) % RemainderTable.PRIME_MOD;
      xAcum = (xAcum * x) % RemainderTable.PRIME_MOD;
    }
    return acum % RemainderTable.PRIME_MOD;
  }

}
